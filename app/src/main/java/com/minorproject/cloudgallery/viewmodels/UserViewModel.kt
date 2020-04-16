package com.minorproject.cloudgallery.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.minorproject.cloudgallery.model.User

class UserViewModel : ViewModel() {
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
}