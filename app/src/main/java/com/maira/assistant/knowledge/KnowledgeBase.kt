package com.maira.assistant.knowledge

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class KnowledgeItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class KnowledgeBase(context: Context) {
    private val prefs = context.getSharedPreferences("maira_knowledge", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveNote(title: String, content: String, tags: List<String> = emptyList()) {
        val items = getAllNotes().toMutableList()
        items.add(KnowledgeItem(title = title, content = content, tags = tags))
        val json = gson.toJson(items)
        prefs.edit().putString("notes", json).apply()
    }
    
    fun getAllNotes(): List<KnowledgeItem> {
        val json = prefs.getString("notes", "[]") ?: "[]"
        val type = object : TypeToken<List<KnowledgeItem>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun searchNotes(query: String): List<KnowledgeItem> {
        return getAllNotes().filter {
            it.title.lowercase().contains(query.lowercase()) ||
            it.content.lowercase().contains(query.lowercase()) ||
            it.tags.any { tag -> tag.lowercase().contains(query.lowercase()) }
        }
    }
}
