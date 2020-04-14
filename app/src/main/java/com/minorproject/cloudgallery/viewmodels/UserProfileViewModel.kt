package com.minorproject.cloudgallery.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.minorproject.cloudgallery.model.User
import com.minorproject.cloudgallery.model.UserInfo

class UserProfileViewModel : ViewModel() {
    companion object {
        private val TAG: String = "UserProfileViewModel"
    }

    private val user: MutableLiveData<User?> = MutableLiveData()
    private val userInfo: MutableLiveData<UserInfo?> = MutableLiveData()

    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        getUserDataFromFirebase()
        readDataFromFireStore()
    }

    fun getUserData(): LiveData<User?> {
        return user
    }

    fun getUserInfoData(): LiveData<UserInfo?> {
        return userInfo
    }

    private fun getUserDataFromFirebase() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user.value = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "UserData: onCancelled")
            }
        })
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
                        userInfo.value = document.toObject(UserInfo::class.java) ?: UserInfo()

                        Log.e("viewModel", "DocumentSnapshot read successfully!")
                    } else {
                        Log.e("viewModel", "No such document!")
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error writing document", e)
            }
    }
}