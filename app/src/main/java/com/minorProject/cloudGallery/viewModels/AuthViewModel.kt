package com.minorProject.cloudGallery.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseAuthHelper
import com.minorProject.cloudGallery.model.repo.Result
import com.minorProject.cloudGallery.model.repo.Success

class AuthViewModel : ViewModel() {

    fun checkIfUserSignInOrNot(): LiveData<Boolean> {
        val result: MutableLiveData<Boolean> = MutableLiveData()
        FirebaseAuthHelper.checkIfUserSignInOrNot()
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        result.value = response.value as Boolean
                    }
                    is Failure -> {
                        result.value = false
                    }
                }
            }
        return result
    }

    fun onRegisterClicked(username: String, email: String, pass: String): LiveData<Result<Any?>>? {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
            FirebaseAuthHelper.onRegisterClicked(username, email, pass)
                .observeForever { response ->
                    when (response) {
                        is Success -> {
                            result.value = Success(response)
                        }
                        is Failure -> {
                            result.value = Failure(response.e)
                        }
                    }
                }
        return result
    }


    fun onLoginClicked(email: String, pass: String): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
            FirebaseAuthHelper.onLoginClicked(email, pass).observeForever { response ->
                when (response) {
                    is Success -> {
                        result.value = Success(response)
                    }
                    is Failure -> {
                        result.value = Failure(response.e)
                    }
                }
            }
        return result
    }

    fun onForgetPassword(email: String?): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        FirebaseAuthHelper.onForgetPassword(email).observeForever { response ->
            when (response) {
                is Success -> {
                    result.value = Success(response)
                }
                is Failure -> {
                    result.value = Failure(response.e)
                }
            }
        }
        return result
    }
}