package com.maira.assistant.history

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class HistoryItem(
    val id: Long = System.currentTimeMillis(),
    val userText: String,
    val mairaResponse: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ConversationHistory(context: Context) {
    private val prefs = context.getSharedPreferences("maira_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun addToHistory(userText: String, response: String) {
        val history = getHistory().toMutableList()
        history.add(0, HistoryItem(userText = userText, mairaResponse = response))
        if (history.size > 100) {
            history.removeAt(history.size - 1)
        }
        val json = gson.toJson(history)
        prefs.edit().putString("history", json).apply()
    }
    
    fun getHistory(): List<HistoryItem> {
        val json = prefs.getString("history", "[]") ?: "[]"
        val type = object : TypeToken<List<HistoryItem>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun clearHistory() {
        prefs.edit().remove("history").apply()
    }
}
