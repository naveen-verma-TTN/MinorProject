package com.minorproject.cloudgallery.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.repo.UploadImage
import com.minorproject.cloudgallery.repo.UploadImageWorker


class UserViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG: String = "UserProfileViewModel"
    }

    private val user: MutableLiveData<User?> = MutableLiveData()

    private var mAuth: FirebaseAuth? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        readDataFromFireStore()
    }

    fun getUserData(): LiveData<User?> {
        return user
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
                        user.value = document.toObject(User::class.java) ?: User()
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
    fun setProfilePic(filePath: Uri) {
        val workManager = WorkManager.getInstance()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
                UploadImageWorker::class.java
            )
            .setInputData(createInputData(filePath))
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .addTag("imageUploadWork")
            .build()
        workManager.enqueue(oneTimeWorkRequest)
    }


    private fun createInputData(url: Uri?): Data {
        return Data.Builder()
            .putString("FILE_URL", url.toString())
            .build()
    }
}