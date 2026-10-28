package com.maira.assistant.documents

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class DocumentReader(private val context: Context) {
    
    fun readTextFile(uri: android.net.Uri): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val text = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                text.append(line).append("\n")
            }
            reader.close()
            inputStream?.close()
            text.toString()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }
    
    fun extractSummary(text: String, maxLength: Int = 200): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength) + "..."
        } else {
            text
        }
    }
}
