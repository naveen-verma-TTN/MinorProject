package com.minorProject.cloudGallery.model.repo

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minorProject.cloudGallery.model.bean.User
import java.util.*

object FirebaseUserDatabaseHelper {
    private val TAG: String = FirebaseUserDatabaseHelper::class.java.name

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storage: FirebaseStorage? = null
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private var storageReference: StorageReference? = null

    init {
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
    }

    /**
     * fun to read user details from firebase
     */
    fun readUserDetailsFromFireStore(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        firebaseFireStore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        firebaseFireStore.collection("UserDetails").document(mAuth.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        result.value = Success(document.toObject(User::class.java) ?: User())
                    } else {
                        result.value = Failure(Exception("No such document!"))
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                    result.value = Failure(ex)
                }
            }.addOnFailureListener { e ->
                result.value = Failure(e)
            }
        return result
    }

    /**
     * fun to update user profile picture
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setProfilePic(context: Context, user: User, uri: Uri): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        val ref =
            storageReference!!.child("${mAuth.uid}/UserProfile/" + mAuth.uid)
        ref.putFile(Compress.getThumbnail(uri, context)!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl
                        .addOnSuccessListener { uri ->
                            user.UserProfile = uri.toString()
                            updateUserDetailsToFireStore(user).observeForever { response ->
                                when (response) {
                                    is Success -> {
                                        result.value = Success(response.value as User)
                                    }
                                    is Failure -> {
                                        result.value = Failure(Exception("Something Went Wrong!"))
                                    }
                                }
                            }
                        }
                } else {
                    Log.e(TAG, "Error happened during the upload process")
                    result.value = Failure(Exception("Error happened during the upload process"))
                }
            }.addOnProgressListener { taskSnapshot ->
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                Log.e("upload:", progress.toString())
            }.addOnFailureListener { e ->
                Log.e("Uploaded", "failed: ${e.message}")
                result.value = Failure(e)
            }
        return result
    }

    /**
     * fun to update user details
     */
    fun updateUserDetailsToFireStore(user: User): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        val userHashMap = HashMap<String, Any>()

        userHashMap["UserId"] = user.UserId
        userHashMap["UserName"] = user.UserName
        userHashMap["UserEmail"] = user.UserEmail
        userHashMap["AccountCreatedOn"] = user.AccountCreatedOn
        userHashMap["UserAdditionalEmail"] = user.UserAdditionalEmail
        userHashMap["UserPhoneNumber"] = user.UserPhoneNumber
        userHashMap["UserDOB"] = user.UserDOB
        userHashMap["UserAddress"] = user.UserAddress
        if (user.UserProfile != "") {
            userHashMap["UserProfile"] = user.UserProfile
        }


        val docIdRef: DocumentReference =
            firebaseFireStore.collection("UserDetails").document(mAuth.uid!!)
        docIdRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    docIdRef.update(userHashMap)
                        .addOnSuccessListener {
                            result.value = Success(user)
                        }
                        .addOnFailureListener { e ->
                            Log.e("status", "failed: " + e.message)
                            result.value = Failure(e)
                        }
                } else {
                    docIdRef.set(userHashMap)
                        .addOnSuccessListener {
                            result.value = Success(user)
                        }
                        .addOnFailureListener { e ->
                            Log.e("status", "failed: " + e.message)
                            result.value = Failure(e)
                        }
                }
            } else {
                result.value = Failure(Exception("Something went wrong!"))
            }
        }
        return result
    }

    /**
     * fun to logOut from the firebase account
     */
    fun logout(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        result.value = Failure(Exception("Something went wrong!"))
        if (mAuth.currentUser != null) {
            mAuth.signOut()
            result.value = Success(true)
        }
        return result
    }
}