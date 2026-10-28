package com.maira.assistant.memory

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class MemoryItem(
    val id: Long = System.currentTimeMillis(),
    val key: String,
    val value: String,
    val type: String, // preference, instruction, fact, event
    val timestamp: Long = System.currentTimeMillis()
)

class MemoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maira_memory", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun saveMemory(key: String, value: String, type: String = "fact") {
        val memories = getAllMemories().toMutableList()
        memories.removeAll { it.key == key }
        memories.add(MemoryItem(key = key, value = value, type = type))
        saveMemories(memories)
    }
    
    fun getMemory(key: String): String? {
        return getAllMemories().find { it.key == key }?.value
    }
    
    fun getAllMemories(): List<MemoryItem> {
        val json = prefs.getString("memories", "[]") ?: "[]"
        val type = object : TypeToken<List<MemoryItem>>() {}.type
        return gson.fromJson(json, type)
    }
    
    private fun saveMemories(memories: List<MemoryItem>) {
        val json = gson.toJson(memories)
        prefs.edit().putString("memories", json).apply()
    }
    
    fun rememberPreference(pref: String, value: String) {
        saveMemory("pref_$pref", value, "preference")
    }
    
    fun rememberInstruction(instr: String) {
        saveMemory("instr_${System.currentTimeMillis()}", instr, "instruction")
    }
    
    fun getRelevantMemories(query: String): List<MemoryItem> {
        return getAllMemories().filter { 
            it.value.lowercase().contains(query.lowercase()) || 
            it.key.lowercase().contains(query.lowercase())
        }.take(5)
    }
}
