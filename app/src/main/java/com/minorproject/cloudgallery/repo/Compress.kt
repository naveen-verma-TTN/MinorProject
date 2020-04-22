package com.minorproject.cloudgallery.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Math.floor

class Compress {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(FileNotFoundException::class, IOException::class)
        fun getThumbnail(uri: Uri, context: Context): Uri? {
            val THUMBNAIL_SIZE = 250.0
            var input: InputStream? = context.contentResolver.openInputStream(uri)
            val onlyBoundsOptions = BitmapFactory.Options()
            onlyBoundsOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
            input?.close()
            if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1) {
                return null
            }
            val originalSize =
                if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth
            val ratio =
                if (originalSize > THUMBNAIL_SIZE) originalSize / THUMBNAIL_SIZE else 1.0
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
            input = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
            input?.close()
            val result = getImageUri(context, bitmap!!)
            return result
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
            val path: String =
                MediaStore.Images.Media.insertImage(
                    inContext.contentResolver,
                    inImage,
                    "data_${System.currentTimeMillis()}.png",
                    null
                )
            return Uri.parse(path)
        }

        private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
            val k = Integer.highestOneBit(kotlin.math.floor(ratio).toInt())
            return if (k == 0) 1 else k
        }
    }
}