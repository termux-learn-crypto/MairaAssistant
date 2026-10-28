package com.maira.assistant.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class OfflineManager(private val context: Context) {
    
    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    fun getOfflineResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("time") -> "समय जानने के लिए घड़ी देखें"
            q.contains("alarm") -> "ऑफलाइन मोड में अलार्म सेट कर सकती हूँ"
            q.contains("calculator") || q.contains("गणना") -> "गणना के लिए कैल्कुलेटर ऐप खोल सकती हूँ"
            q.contains("hello") || q.contains("namaste") -> "नमस्ते! मैं ऑफलाइन मोड में हूँ"
            else -> "ऑफलाइन मोड में मैं सीमित सुविधाएं प्रदान कर सकती हूँ। इंटरनेट कनेक्शन होने पर बेहतर मदद कर सकूँगी।"
        }
    }
}
