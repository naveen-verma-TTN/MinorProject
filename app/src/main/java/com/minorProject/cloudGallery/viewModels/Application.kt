
package com.minorProject.cloudGallery.viewModels

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * Application class
 */
class Application : Application(){
    private val channelId = "Progress Upload Images Notification"

    override fun onCreate(){
        super.onCreate()
        createNotificationChannels()
    }

    /**
     * function to setting up the notification
     */
    private fun createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Progress Upload Images Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Progress Upload Images Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager!!.createNotificationChannel(channel)
        }
    }
}



