package com.minorproject.cloudgallery.repo

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class UploadImageWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val value = inputData.getString("FILE_URL")
        return try {
            Log.d(TAG, "Uploading Work Started")
            val myUri: Uri = Uri.parse(value)
            UploadImage.saveUserProfilePicToFireStore(context, myUri)
            Result.success()
        } catch (throwable: Throwable) {
            Log.d(TAG, "Error: " + throwable.message)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "UploadImageWorker"
    }
}