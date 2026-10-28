package com.maira.assistant.appcontrol

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppControlManager(private val context: Context) {
    
    private val appMap = mapOf(
        "chrome" to "com.android.chrome",
        "browser" to "com.android.chrome",
        "youtube" to "com.google.android.youtube",
        "whatsapp" to "com.whatsapp",
        "instagram" to "com.instagram.android",
        "facebook" to "com.facebook.katana",
        "gmail" to "com.google.android.gm",
        "maps" to "com.google.android.apps.maps",
        "google maps" to "com.google.android.apps.maps",
        "phone" to "com.android.dialer",
        "messages" to "com.google.android.apps.messaging",
        "camera" to "com.android.camera2",
        "settings" to "com.android.settings",
        "calculator" to "com.android.calculator2",
        "gallery" to "com.google.android.apps.photos",
        "play store" to "com.android.vending"
    )
    
    fun openApp(appName: String): Boolean {
        val name = appName.lowercase()
        val packageName = appMap[name] ?: findPackageByName(name)
        
        if (packageName.isNullOrEmpty()) return false
        
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } else {
            false
        }
    }
    
    private fun findPackageByName(name: String): String? {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            val appLabel = pm.getApplicationLabel(app).toString().lowercase()
            if (appLabel.contains(name)) {
                return app.packageName
            }
        }
        return null
    }
    
    fun canOpenApp(appName: String): Boolean {
        val name = appName.lowercase()
        val packageName = appMap[name]
        if (packageName != null) {
            return isAppInstalled(packageName)
        }
        return findPackageByName(name) != null
    }
    
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
