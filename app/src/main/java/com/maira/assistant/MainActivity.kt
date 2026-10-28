package com.maira.assistant

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.maira.assistant.core.AssistantState
import com.maira.assistant.service.MairaForegroundService
import android.widget.TextView
import android.view.View

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvStatus: TextView
    private lateinit var tvUserText: TextView
    private lateinit var tvMairaResponse: TextView
    private lateinit var micIndicator: View
    private lateinit var btnStart: MaterialButton
    private lateinit var btnStop: MaterialButton
    
    private val permissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()
    
    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stateName = intent?.getStringExtra("state") ?: return
            val state = AssistantState.valueOf(stateName)
            updateUI(state)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupButtons()
        requestPermissions()
        
        val filter = IntentFilter("com.maira.assistant.STATE_UPDATE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stateReceiver, filter)
        }
    }
    
    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvUserText = findViewById(R.id.tvUserText)
        tvMairaResponse = findViewById(R.id.tvMairaResponse)
        micIndicator = findViewById(R.id.micIndicator)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
    }
    
    private fun setupButtons() {
        btnStart.setOnClickListener {
            if (hasPermissions()) {
                startMairaService()
                Toast.makeText(this, "Maira started - Say Hey Maira", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissions()
            }
        }
        
        btnStop.setOnClickListener {
            stopMairaService()
            Toast.makeText(this, "Maira stopped", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startMairaService() {
        val intent = Intent(this, MairaForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    
    private fun stopMairaService() {
        val intent = Intent(this, MairaForegroundService::class.java)
        stopService(intent)
        tvStatus.text = "Stopped"
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_idle))
        micIndicator.background.setTint(ContextCompat.getColor(this, R.color.status_idle))
    }
    
    private fun requestPermissions() {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 100)
        }
    }
    
    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun updateUI(state: AssistantState) {
        when (state) {
            AssistantState.IDLE_STANDBY -> {
                tvStatus.text = "Standby - Say Hey Maira"
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_idle))
                micIndicator.background.setTint(ContextCompat.getColor(this, R.color.status_idle))
            }
            AssistantState.LISTENING_COMMAND -> {
                tvStatus.text = "Listening..."
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_listening))
                micIndicator.background.setTint(ContextCompat.getColor(this, R.color.status_listening))
            }
            AssistantState.PROCESSING -> {
                tvStatus.text = "Processing..."
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_processing))
                micIndicator.background.setTint(ContextCompat.getColor(this, R.color.status_processing))
            }
            AssistantState.SPEAKING -> {
                tvStatus.text = "Speaking..."
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_speaking))
                micIndicator.background.setTint(ContextCompat.getColor(this, R.color.status_speaking))
            }
            else -> {
                tvStatus.text = "Ready"
                tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_idle))
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(stateReceiver)
        } catch (e: Exception) {
        }
    }
}
