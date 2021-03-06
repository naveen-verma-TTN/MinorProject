package com.minorProject.cloudGallery.model.repo.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.*

/**
 * class to Compress image
 */
@Suppress("DEPRECATION")
object Compress {
    private const val THUMBNAIL_SIZE = 250.0

    @Throws(FileNotFoundException::class, IOException::class)
    fun getThumbnail(uri: Uri, context: Context): Uri? {
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
        bitmapOptions.inSampleSize =
            getPowerOfTwoForSampleRatio(
                ratio
            )
        input = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input?.close()
        return getImageUri(
            bitmap!!,
            "thumb_",
            "${System.currentTimeMillis()}.png"
        )
    }

    fun getImageUri(inImage: Bitmap, preName: String, suffixName: String): Uri {
        val tempDir: File = Environment.getExternalStorageDirectory()
        tempDir.mkdir()
        val tempFile: File = File.createTempFile(preName, suffixName)
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fos = FileOutputStream(tempFile)
        fos.write(bitmapData)
        fos.flush()
        fos.close()
        return Uri.fromFile(tempFile)
    }

    private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
        val k = Integer.highestOneBit(kotlin.math.floor(ratio).toInt())
        return if (k == 0) 1 else k
    }
}