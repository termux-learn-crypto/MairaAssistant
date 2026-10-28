package com.maira.assistant.security

import android.content.Context
import android.content.SharedPreferences

class SecurityManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maira_security", Context.MODE_PRIVATE)
    
    fun isMicEnabled(): Boolean = prefs.getBoolean("mic_enabled", true)
    fun setMicEnabled(enabled: Boolean) { prefs.edit().putBoolean("mic_enabled", enabled).apply() }
    fun isPrivacyModeEnabled(): Boolean = prefs.getBoolean("privacy_mode", false)
    fun enablePrivacyMode() { prefs.edit().putBoolean("privacy_mode", true).apply() }
    fun clearAllPrivateData() { prefs.edit().clear().apply() }
}
