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
    fun saveUserProfilePicToFireStore(
        context: Context,
        filePath: Uri?,
        compress: Boolean
    ): String? {
        var downloadUri: String? = null
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        if (filePath != null) {
            val ref =
                storageReference!!.child("${mAuth?.uid}/UserProfile/" + mAuth?.uid)
            if (compress) {
                ref.putFile(Compress.getThumbnail(filePath, context)!!)
            } else {
                ref.putFile(filePath)
            }
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
}