package com.maira.assistant.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.maira.assistant.R
import com.maira.assistant.knowledge.KnowledgeBase

class KnowledgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge)
        
        val kb = KnowledgeBase(this)
        val listView = findViewById<ListView>(R.id.knowledgeListView)
        val items = kb.getAllNotes().map { "${it.title}\n${it.content}" }
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        
        title = "Personal Knowledge Base"
    }
}
