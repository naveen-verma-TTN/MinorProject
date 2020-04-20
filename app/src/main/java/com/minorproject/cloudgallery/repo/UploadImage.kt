package com.minorproject.cloudgallery.repo

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minorproject.cloudgallery.model.User
import java.io.*
import java.util.*
import kotlin.math.floor


object UploadImage {
    private var mAuth: FirebaseAuth? = null

    var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    init {
        mAuth = FirebaseAuth.getInstance()
    }

    private const val TAG: String = "UploadImage"


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveUserProfilePicToFireStore(context: Context, filePath: Uri?): String? {
        var downloadUri: String? = null
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        if (filePath != null) {
            val ref =
                storageReference!!.child("${mAuth?.uid}/UserProfile/" + mAuth?.uid)

            /*       ref.putFile(getThumbnail(filePath, context)!!)*/

            ref.putFile(filePath)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.downloadUrl
                            .addOnSuccessListener { uri ->
                                val user = HashMap<String, Any>()
                                downloadUri = uri.toString()

                                user["UserProfile"] = downloadUri!!

                                val rootRef = FirebaseFirestore.getInstance()
                                val docIdRef: DocumentReference =
                                    rootRef.collection("UserDetails")
                                        .document(mAuth?.uid!!)
                                docIdRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document!!.exists()) {
                                            docIdRef.update(user)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot successfully written!"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(
                                                        TAG, "Error writing document", e
                                                    )
                                                }
                                        } else {
                                            Log.d(TAG, "Failed with: ", task.exception)
                                        }
                                    }
                                }
                            }
                    } else {
                        Log.e(
                            "Uploaded",
                            "Error happened during the upload process"
                        )
                    }
                }.addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                    Log.e("upload:", progress.toString())
                }.addOnFailureListener { e ->
                    Log.e("Uploaded", "failed: ${e.message}")
                }
        }
        return downloadUri
    }

    /*  @RequiresApi(Build.VERSION_CODES.O)
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
          val k = Integer.highestOneBit(floor(ratio).toInt())
          return if (k == 0) 1 else k
      }*/
}