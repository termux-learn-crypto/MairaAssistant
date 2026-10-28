package com.maira.assistant.settings

import android.content.Context
import android.content.SharedPreferences

data class VoiceConfig(
    val language: String = "hi-IN",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val voiceName: String = ""
)

class VoiceSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maira_voice_settings", Context.MODE_PRIVATE)
    
    fun saveVoiceConfig(config: VoiceConfig) {
        prefs.edit()
            .putString("language", config.language)
            .putFloat("speechRate", config.speechRate)
            .putFloat("pitch", config.pitch)
            .putString("voiceName", config.voiceName)
            .apply()
    }
    
    fun getVoiceConfig(): VoiceConfig {
        return VoiceConfig(
            language = prefs.getString("language", "hi-IN") ?: "hi-IN",
            speechRate = prefs.getFloat("speechRate", 1.0f),
            pitch = prefs.getFloat("pitch", 1.0f),
            voiceName = prefs.getString("voiceName", "") ?: ""
        )
    }
    
    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
    }
    
    fun setSpeechRate(rate: Float) {
        prefs.edit().putFloat("speechRate", rate).apply()
    }
}
