package com.maira.assistant.smarthome

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

data class SmartDevice(
    val id: String,
    val name: String,
    val type: String, // light, fan, switch, plug
    val ipAddress: String,
    val isOn: Boolean = false
)

class SmartHomeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maira_smarthome", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val client = OkHttpClient()
    
    fun addDevice(device: SmartDevice) {
        val devices = getDevices().toMutableList()
        devices.add(device)
        saveDevices(devices)
    }
    
    fun getDevices(): List<SmartDevice> {
        val json = prefs.getString("devices", "[]") ?: "[]"
        val type = object : TypeToken<List<SmartDevice>>() {}.type
        return gson.fromJson(json, type)
    }
    
    private fun saveDevices(devices: List<SmartDevice>) {
        val json = gson.toJson(devices)
        prefs.edit().putString("devices", json).apply()
    }
    
    suspend fun controlDevice(deviceName: String, action: String): Boolean = withContext(Dispatchers.IO) {
        val device = getDevices().find { it.name.lowercase() == deviceName.lowercase() }
        if (device == null) return@withContext false
        
        try {
            val url = "http://${device.ipAddress}/$action"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
