package com.minorProject.cloudGallery.model.repo

import androidx.lifecycle.LiveData

/**
 * Firebase Auth Repository interface
 */
interface FirebaseAuthRepository {
    fun checkIfUserSignInOrNot(): LiveData<Result<Any?>>
    fun onRegisterClicked(username: String, email: String, pass: String): LiveData<Result<Any?>>
    fun verifyEmail(): LiveData<Result<Any?>>
    fun onLoginClicked(email: String, pwd: String): LiveData<Result<Any?>>
    fun onForgetPassword(email: String?): LiveData<Result<Any?>>
}