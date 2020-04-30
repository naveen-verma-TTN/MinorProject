package com.minorProject.cloudGallery.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseNetworkClass
import com.minorProject.cloudGallery.model.repo.Result
import com.minorProject.cloudGallery.model.repo.Success

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    companion object{
        private val TAG: String = AuthViewModel::class.java.name
    }

    fun onRegisterClicked(username: String, email: String, pass: String): LiveData<Result<Any?>>? {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
            FirebaseNetworkClass.onRegisterClicked(username, email, pass)
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
        FirebaseNetworkClass.onLoginClicked(email, pass).observeForever { response->
            when(response){
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
        FirebaseNetworkClass.onForgetPassword(email).observeForever { response ->
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