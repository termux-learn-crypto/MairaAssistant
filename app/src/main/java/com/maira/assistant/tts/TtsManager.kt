package com.maira.assistant.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TtsManager(private val context: Context) {
    private val tag = "TtsManager"
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private var listener: TtsListener? = null
    
    interface TtsListener {
        fun onSpeakingStarted()
        fun onSpeakingCompleted()
        fun onSpeakingError(error: String)
    }
    
    fun initialize(ttsListener: TtsListener) {
        listener = ttsListener
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("hi", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Try English
                    val engResult = textToSpeech?.setLanguage(Locale.ENGLISH)
                    if (engResult == TextToSpeech.LANG_MISSING_DATA || engResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(tag, "Language not supported")
                        listener?.onSpeakingError("TTS language not supported")
                    } else {
                        isInitialized = true
                        Log.d(tag, "TTS initialized with English")
                    }
                } else {
                    isInitialized = true
                    Log.d(tag, "TTS initialized with Hindi")
                }
                
                setupUtteranceListener()
            } else {
                Log.e(tag, "TTS initialization failed")
                listener?.onSpeakingError("TTS initialization failed")
            }
        }
    }
    
    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                listener?.onSpeakingStarted()
            }
            
            override fun onDone(utteranceId: String?) {
                listener?.onSpeakingCompleted()
            }
            
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                listener?.onSpeakingError("TTS playback error")
            }
            
            override fun onError(utteranceId: String?, errorCode: Int) {
                listener?.onSpeakingError("TTS playback error: $errorCode")
            }
        })
    }
    
    fun speak(text: String) {
        if (!isInitialized || textToSpeech == null) {
            listener?.onSpeakingError("TTS not initialized")
            return
        }
        
        if (text.isEmpty()) {
            listener?.onSpeakingCompleted()
            return
        }
        
        val utteranceId = "maira_utterance_${System.currentTimeMillis()}"
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }
    
    fun stop() {
        textToSpeech?.stop()
    }
    
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    fun setSpeechRate(rate: Float) {
        textToSpeech?.setSpeechRate(rate)
    }
    
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        isInitialized = false
    }
}
