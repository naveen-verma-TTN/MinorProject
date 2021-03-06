package com.minorProject.cloudGallery

import android.app.Application

/**
 * Application class ------- to get Application instance
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: MyApplication
        fun getMyInstance(): MyApplication {
            return instance
        }
    }
}