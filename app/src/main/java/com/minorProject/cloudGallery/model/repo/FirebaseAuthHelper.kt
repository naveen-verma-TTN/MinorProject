package com.minorProject.cloudGallery.model.repo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.minorProject.cloudGallery.model.bean.User
import java.text.SimpleDateFormat
import java.util.*

object FirebaseAuthHelper {
    private val mAuth = FirebaseAuth.getInstance()

    private val TAG: String = FirebaseAuthHelper::class.java.name

    fun checkIfUserSignInOrNot(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        result.value = Success(false)
        if (mAuth.currentUser != null) {
            result.value = Success(true)
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun onRegisterClicked(username: String, email: String, pass: String): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    val profileUpdates: UserProfileChangeRequest =
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(username).build()

                    firebaseUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener {
                            if (task.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                            }
                        }
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = mAuth.currentUser!!.uid

                    val user = User(
                        UserId = userId,
                        UserName = username,
                        UserEmail = email,
                        AccountCreatedOn = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                            .format(Date())
                    )
                    FirebaseUserDatabaseHelper.updateUserDetailsToFireStore(user).observeForever {
                        when(it){
                            is Success ->{
                                verifyEmail().observeForever { response ->
                                    when (response) {
                                        is Success -> {
                                            result.value = Success(response)
                                        }
                                        is Failure -> {
                                            result.value = Failure(response.e)
                                        }
                                    }
                                }
                            }
                            is Failure ->{
                                Log.w(TAG, "UserDetailUpdating:failure", it.e)
                                result.value = Failure(Exception(it.e))
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    result.value = Failure(Exception(task.exception))
                }
            }
        return result
    }

    private fun verifyEmail(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        mAuth.currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Success(true)
                } else {
                    Log.e(
                        ContentValues.TAG,
                        "Failed to send verification email.",
                        task.exception
                    )
                    result.value = Failure(Exception(task.exception))
                }
            }
        return result
    }

    fun onLoginClicked(email: String, pwd: String): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        mAuth.signInWithEmailAndPassword(email, pwd)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Success(true)
                } else {
                    result.value = Failure(Exception(task.exception))
                }
            }
        return result
    }

    fun onForgetPassword(email: String?): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        mAuth.sendPasswordResetEmail(email.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.value = Success(true)
                } else {
                    result.value = Failure(Exception(task.exception))
                }
            }
        return result
    }
}