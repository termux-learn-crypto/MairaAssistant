package com.maira.assistant.interruption

import android.util.Log

class InterruptionHandler {
    private val tag = "InterruptionHandler"
    private var isSpeaking = false
    
    fun onUserInterrupt() {
        Log.d(tag, "User interruption")
        if (isSpeaking) stopSpeaking()
    }
    
    fun stopSpeaking() { isSpeaking = false }
    fun setSpeakingState(speaking: Boolean) { isSpeaking = speaking }
}
