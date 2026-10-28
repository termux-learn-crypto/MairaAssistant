package com.maira.assistant.aibrain

import android.content.Context
import com.maira.assistant.context.ContextManager
import com.maira.assistant.memory.MemoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AIBrain(context: Context) {
    private val memoryManager = MemoryManager(context)
    private val contextManager = ContextManager(context)
    
    suspend fun think(userQuery: String): String = withContext(Dispatchers.IO) {
        val contextSummary = contextManager.getContextSummary()
        val relevantMemories = memoryManager.getRelevantMemories(userQuery)
        generateResponse(userQuery)
    }
    
    private fun generateResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("namaste") || q.contains("hello") || q.contains("hi") -> 
                "नमस्ते! मैं मैरा हूँ। बताइए मैं आपकी कैसे मदद कर सकती हूँ?"
            q.contains("kaise ho") -> 
                "मैं बिल्कुल ठीक हूँ, धन्यवाद पूछने के लिए। आप बताइए कैसे हैं?"
            q.contains("tera naam") || q.contains("your name") -> 
                "मेरा नाम मैरा है। मैं आपका personal voice assistant हूँ।"
            q.contains("dhanyavaad") || q.contains("thank") -> 
                "खुशी हुई मदद करके! और कुछ चाहिए हो तो बोलिए।"
            q.contains("bye") || q.contains("alvida") -> 
                "अलविदा! फिर मिलेंगे।"
            else -> 
                "मैं समझ गई। मैं आपकी मदद करने की कोशिश कर रही हूँ।"
        }
    }
    
    fun saveToMemory(userQuery: String, response: String) {
        contextManager.addTurn(userQuery, response)
    }
}
