
package com.minorproject.cloudgallery.viewmodels

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Application : Application(){
    private val channelId = "Progress Notification"

    override fun onCreate(){
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                channelId,
                "Progress Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "Progress Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager!!.createNotificationChannel(channel1)
        }
    }
}



