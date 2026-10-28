package com.maira.assistant.ui

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maira.assistant.R
import com.maira.assistant.settings.VoiceSettings
import com.maira.assistant.security.SecurityManager

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        val voiceSettings = VoiceSettings(this)
        val securityManager = SecurityManager(this)
        
        title = "Maira Settings"
    }
}
