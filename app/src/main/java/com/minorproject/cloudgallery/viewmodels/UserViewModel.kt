package com.minorproject.cloudgallery.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.repo.Compress
import java.util.HashMap


class UserViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "UserProfileViewModel"
    }

    val userMutableLiveData: MutableLiveData<User> = MutableLiveData()

    private var mAuth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        readDataFromFireStore()
    }


    private fun readDataFromFireStore() {
        val fireStore = FirebaseFirestore.getInstance()
        fireStore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        fireStore
            .collection("UserDetails").document(mAuth?.currentUser?.uid!!)
            .get()
            .addOnSuccessListener { document ->
                try {
                    if (document != null) {
                        userMutableLiveData.value = document.toObject(User::class.java) ?: User()
                        Log.d("viewModel", "DocumentSnapshot read successfully!")
                    } else {
                        Log.e("viewModel", "No such document!")
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message!!)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error writing document", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setProfilePic(uri: Uri) {
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        val ref =
            storageReference!!.child("${mAuth?.uid}/UserProfile/" + mAuth?.uid)
            ref.putFile(Compress.getThumbnail(uri, getApplication())!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl
                        .addOnSuccessListener { uri ->
                            val user = userMutableLiveData.value
                            user?.UserProfile = uri.toString()
                            userMutableLiveData.value = user
                            updateUserDetails(user!!)
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


    fun updateUserDetails(user: User): LiveData<Boolean> {
        val result: MutableLiveData<Boolean> = MutableLiveData()
        val userHashMap = HashMap<String, Any>()
        mAuth = FirebaseAuth.getInstance()

        userHashMap["UserAdditionalEmail"] = user.UserAdditionalEmail
        userHashMap["UserPhoneNumber"] = user.UserPhoneNumber
        userHashMap["UserDOB"] = user.UserDOB
        userHashMap["UserAddress"] = user.UserAddress
        if(user.UserProfile != ""){
            userHashMap["UserProfile"] = user.UserProfile
        }

        val rootRef = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference =
            rootRef.collection("UserDetails").document(mAuth?.uid!!)
        docIdRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    docIdRef.update(userHashMap)
                        .addOnSuccessListener {
                            Log.e("status", "success")
                            result.value = true
                        }
                        .addOnFailureListener { e ->
                            Log.e("status", "failed: "+ e.message)
                            result.value = false
                        }
                }
            } else {
                result.value = false
            }
        }
        return result
    }
}