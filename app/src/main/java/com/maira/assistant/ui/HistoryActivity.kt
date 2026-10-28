package com.maira.assistant.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.maira.assistant.R
import com.maira.assistant.history.ConversationHistory

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        val history = ConversationHistory(this)
        val listView = findViewById<ListView>(R.id.historyListView)
        val items = history.getHistory().map { "${it.userText}\n→ ${it.mairaResponse}" }
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        
        title = "Conversation History"
    }
}
