package com.maira.assistant.stt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SttManager(private val context: Context) {
    private val tag = "SttManager"
    private var speechRecognizer: SpeechRecognizer? = null
    private var listener: SttListener? = null
    private var isListening = false
    
    interface SttListener {
        fun onResult(text: String)
        fun onError(error: String)
        fun onPartialResult(text: String)
    }
    
    fun initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
            Log.d(tag, "SpeechRecognizer initialized")
        } else {
            Log.e(tag, "Speech recognition not available")
        }
    }
    
    fun startListening(sttListener: SttListener) {
        listener = sttListener
        if (speechRecognizer == null) {
            initialize()
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("hi", "IN"))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        try {
            speechRecognizer?.startListening(intent)
            isListening = true
            Log.d(tag, "Started listening for speech")
        } catch (e: Exception) {
            Log.e(tag, "Error starting listening", e)
            listener?.onError("Failed to start speech recognition")
        }
    }
    
    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
    
    private fun createRecognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(tag, "Ready for speech")
        }
        
        override fun onBeginningOfSpeech() {
            Log.d(tag, "Beginning of speech")
        }
        
        override fun onRmsChanged(rmsdB: Float) {}
        
        override fun onBufferReceived(buffer: ByteArray?) {}
        
        override fun onEndOfSpeech() {
            Log.d(tag, "End of speech")
            isListening = false
        }
        
        override fun onError(error: Int) {
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e(tag, "Error: $errorMessage")
            listener?.onError(errorMessage)
            isListening = false
        }
        
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                Log.d(tag, "Result: $text")
                listener?.onResult(text)
            } else {
                listener?.onError("No results")
            }
            isListening = false
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                listener?.onPartialResult(matches[0])
            }
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
