package com.maira.assistant.filemanager

import android.content.Context
import android.os.Environment
import java.io.File

class FileManager(private val context: Context) {
    
    fun searchFiles(query: String): List<File> {
        val results = mutableListOf<File>()
        val externalDir = Environment.getExternalStorageDirectory()
        searchRecursive(externalDir, query, results)
        return results.take(20)
    }
    
    private fun searchRecursive(directory: File, query: String, results: MutableList<File>) {
        if (results.size >= 20) return
        try {
            val files = directory.listFiles() ?: return
            for (file in files) {
                if (file.isDirectory && !file.name.startsWith(".")) {
                    searchRecursive(file, query, results)
                } else if (file.name.lowercase().contains(query.lowercase())) {
                    results.add(file)
                }
            }
        } catch (e: Exception) {
        }
    }
    
    fun readFile(file: File): String {
        return try {
            if (file.length() > 100000) {
                "फाइल बहुत बड़ी है"
            } else {
                file.readText()
            }
        } catch (e: Exception) {
            "फाइल पढ़ने में त्रुटि"
        }
    }
}
