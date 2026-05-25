package com.markdownpilot.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.markdownpilot.app.util.C
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MarkdownPilotApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(C.NOTIFICATION_CHANNEL, "Document Generation", NotificationManager.IMPORTANCE_LOW)
        (getSystemService(NotificationManager::class.java)).createNotificationChannel(channel)
    }
}
