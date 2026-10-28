package com.maira.assistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.maira.assistant.MainActivity
import com.maira.assistant.R
import com.maira.assistant.core.AssistantState
import com.maira.assistant.core.AudioSessionManager
import com.maira.assistant.stt.SttManager
import com.maira.assistant.tts.TtsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MairaForegroundService : Service() {
    private val tag = "MairaForegroundService"
    private val channelId = "maira_service_channel"
    private val notificationId = 1001
    
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var audioSessionManager: AudioSessionManager
    private lateinit var sttManager: SttManager
    private lateinit var ttsManager: TtsManager
    
    private var currentState = AssistantState.IDLE_STANDBY
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service created")
        
        audioSessionManager = AudioSessionManager(this)
        sttManager = SttManager(this)
        ttsManager = TtsManager(this)
        
        sttManager.initialize()
        ttsManager.initialize(object : TtsManager.TtsListener {
            override fun onSpeakingStarted() {
                currentState = AssistantState.SPEAKING
                updateState()
            }
            
            override fun onSpeakingCompleted() {
                Log.d(tag, "TTS completed, resuming wake word detection")
                serviceScope.launch {
                    delay(500)
                    resumeWakeWordDetection()
                }
            }
            
            override fun onSpeakingError(error: String) {
                Log.e(tag, "TTS error: $error")
                resumeWakeWordDetection()
            }
        })
        
        acquireWakeLock()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "Service started")
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(notificationId, notification)
        }
        
        startWakeWordDetection()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "Service destroyed")
        sttManager.destroy()
        ttsManager.shutdown()
        audioSessionManager.abandonFocus()
        releaseWakeLock()
    }
    
    private fun startWakeWordDetection() {
        currentState = AssistantState.IDLE_STANDBY
        updateState()
        // Wake word detection would start here
        Log.d(tag, "Waiting for wake word: Hey Maira or Maira")
    }
    
    private fun resumeWakeWordDetection() {
        currentState = AssistantState.IDLE_STANDBY
        updateState()
        audioSessionManager.abandonFocus()
        Log.d(tag, "Resumed wake word detection")
    }
    
    private fun startCommandListening() {
        currentState = AssistantState.LISTENING_COMMAND
        updateState()
        audioSessionManager.requestFocusForRecording()
        
        sttManager.startListening(object : SttManager.SttListener {
            override fun onResult(text: String) {
                Log.d(tag, "Command recognized: $text")
                processCommand(text)
            }
            
            override fun onError(error: String) {
                Log.e(tag, "STT error: $error")
                resumeWakeWordDetection()
            }
            
            override fun onPartialResult(text: String) {
                Log.d(tag, "Partial: $text")
            }
        })
    }
    
    private fun processCommand(command: String) {
        currentState = AssistantState.PROCESSING
        updateState()
        audioSessionManager.abandonFocus()
        
        serviceScope.launch {
            delay(500)
            generateResponse(command)
        }
    }
    
    private fun generateResponse(command: String) {
        val response = when {
            command.lowercase().contains("namaste") || command.lowercase().contains("hello") -> 
                "नमस्ते! मैं मैरा हूँ, आपकी कैसे मदद कर सकती हूँ?"
            command.lowercase().contains("kaise ho") || command.lowercase().contains("how are you") ->
                "मैं बिल्कुल ठीक हूँ धन्यवाद! आप बताइए कैसे हैं?"
            command.lowercase().contains("time") || command.lowercase().contains("samay") ->
                "अभी समय देख रही हूँ"
            else ->
                "मैं समझ गई हूँ। आप और कुछ पूछना चाहेंगे?"
        }
        
        speakResponse(response)
    }
    
    private fun speakResponse(response: String) {
        currentState = AssistantState.SPEAKING
        updateState()
        audioSessionManager.requestFocusForPlayback()
        ttsManager.speak(response)
    }
    
    private fun updateState() {
        val intent = Intent("com.maira.assistant.STATE_UPDATE")
        intent.putExtra("state", currentState.name)
        sendBroadcast(intent)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Maira Assistant Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maira voice assistant background service"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MairaAssistant::WakeLock"
        )
        wakeLock?.acquire(10 * 60 * 1000L)
    }
    
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }
}
