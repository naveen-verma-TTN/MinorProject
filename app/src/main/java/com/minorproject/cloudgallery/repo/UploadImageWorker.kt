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
        val uri = Uri.parse(inputData.getString("FILE_URI"))
        val compress = inputData.getBoolean("COMPRESS", false)
        return try {
            Log.d(TAG, "Uploading Work Started")
            UploadImage.saveUserProfilePicToFireStore(context, uri,compress)
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