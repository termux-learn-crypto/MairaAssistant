package com.maira.assistant.vad

import kotlin.math.abs

class VadDetector {
    private var isSpeaking = false
    private var silenceFrames = 0
    private var speechFrames = 0
    
    private val silenceThreshold = 700 // ms worth of frames
    private val speechThreshold = 150 // ms worth of frames
    private val frameSize = 160 // 10ms at 16kHz
    private val rmsThreshold = 500.0
    
    fun processFrame(audioData: ShortArray): Boolean {
        val rms = calculateRMS(audioData)
        val isSpeechFrame = rms > rmsThreshold
        
        if (isSpeechFrame) {
            speechFrames++
            silenceFrames = 0
            if (!isSpeaking && speechFrames >= (speechThreshold / 10)) {
                isSpeaking = true
            }
        } else {
            silenceFrames++
            speechFrames = 0
            if (isSpeaking && silenceFrames >= (silenceThreshold / 10)) {
                isSpeaking = false
                silenceFrames = 0
                speechFrames = 0
                return true // End of speech detected
            }
        }
        return false
    }
    
    fun isCurrentlySpeaking(): Boolean = isSpeaking
    
    fun reset() {
        isSpeaking = false
        silenceFrames = 0
        speechFrames = 0
    }
    
    private fun calculateRMS(audioData: ShortArray): Double {
        if (audioData.isEmpty()) return 0.0
        var sum = 0.0
        for (sample in audioData) {
            val valDouble = sample.toDouble()
            sum += valDouble * valDouble
        }
        val mean = sum / audioData.size
        return Math.sqrt(mean)
    }
}
