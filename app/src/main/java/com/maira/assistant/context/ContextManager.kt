package com.maira.assistant.context

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ConversationTurn(
    val userMessage: String,
    val assistantResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ContextManager(context: Context) {
    private val prefs = context.getSharedPreferences("maira_context", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val maxTurns = 10
    
    fun addTurn(userMessage: String, assistantResponse: String) {
        val turns = getRecentTurns().toMutableList()
        turns.add(ConversationTurn(userMessage, assistantResponse))
        if (turns.size > maxTurns) {
            turns.removeAt(0)
        }
        val json = gson.toJson(turns)
        prefs.edit().putString("turns", json).apply()
    }
    
    fun getRecentTurns(): List<ConversationTurn> {
        val json = prefs.getString("turns", "[]") ?: "[]"
        val type = object : TypeToken<List<ConversationTurn>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun getContextSummary(): String {
        val turns = getRecentTurns()
        if (turns.isEmpty()) return ""
        val sb = StringBuilder()
        sb.append("Previous conversation:\n")
        turns.forEach { turn ->
            sb.append("User: ${turn.userMessage}\n")
            sb.append("Maira: ${turn.assistantResponse}\n\n")
        }
        return sb.toString()
    }
    
    fun clearContext() {
        prefs.edit().remove("turns").apply()
    }
}
