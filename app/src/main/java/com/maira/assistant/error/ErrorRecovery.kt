package com.maira.assistant.error

import android.util.Log
import kotlinx.coroutines.delay

class ErrorRecovery {
    private val tag = "ErrorRecovery"
    private val maxRetries = 3
    private val retryDelay = 1000L
    
    suspend fun <T> withRetry(operation: suspend () -> T): Result<T> {
        var lastException: Exception? = null
        for (attempt in 1..maxRetries) {
            try {
                val result = operation()
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e
                Log.e(tag, "Attempt $attempt failed", e)
                if (attempt < maxRetries) delay(retryDelay * attempt)
            }
        }
        return Result.failure(lastException ?: Exception("Unknown error"))
    }
    
    fun handleError(error: Throwable): String {
        return when {
            error.message?.contains("permission", true) == true -> "अनुमति की जरूरत है।"
            error.message?.contains("network", true) == true -> "इंटरनेट समस्या है।"
            error.message?.contains("microphone", true) == true -> "माइक्रोफोन समस्या है।"
            else -> "कुछ त्रुटि हुई। फिर से प्रयास करता हूँ।"
        }
    }
}
