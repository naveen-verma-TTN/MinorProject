package com.minorProject.cloudGallery.viewModels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseDatabaseHelper
import com.minorProject.cloudGallery.model.repo.Result
import com.minorProject.cloudGallery.model.repo.Success

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val user: MutableLiveData<User> = MutableLiveData()

    companion object {
        private val TAG: String = UserViewModel::class.java.name
    }

    init {
        readUserDetailsFromFireStore()
    }

    fun getUserDetails(): LiveData<User?> = user

    private fun readUserDetailsFromFireStore() =
        FirebaseDatabaseHelper.readUserDetailsFromFireStore().observeForever { response ->
            when (response) {
                is Success -> {
                    user.value = response.value as User
                }
                is Failure -> {
                    Log.e(TAG, response.e.message.toString())
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setProfilePic(data: Uri) {
        FirebaseDatabaseHelper.setProfilePic(context, user.value!!, data)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        user.value = response.value as User
                    }
                    is Failure -> {
                        Log.e(TAG, response.e.message.toString())
                    }
                }
            }
    }

    fun updateUserDetailsToFireStore(userDefault: User): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        FirebaseDatabaseHelper.updateUserDetailsToFireStore(userDefault)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        user.value = response.value as User
                        result.value = Success(response.value)
                    }
                    is Failure -> {
                        Log.e(TAG, response.e.message.toString())
                        result.value = Failure(response.e)
                    }
                }
            }
        return result
    }

    fun logout(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        FirebaseDatabaseHelper.logout()
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
}