package com.minorproject.cloudgallery.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.minorproject.cloudgallery.model.User

class UserProfileViewModel : ViewModel() {
    companion object {
        private val TAG: String = "UserProfileViewModel"
    }

    private val user: MutableLiveData<User?> = MutableLiveData()

    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null

    init {
        getUserDataFromFirebase()
    }

    fun getUserData(): LiveData<User?> {
        return user
    }

    private fun getUserDataFromFirebase() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

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
}