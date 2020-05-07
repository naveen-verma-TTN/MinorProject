package com.minorProject.cloudGallery.viewModels

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minorProject.cloudGallery.model.bean.User
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseUserRepository
import com.minorProject.cloudGallery.model.repo.Result
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.ShowToast

class UserViewModel(
    private val context: Context,
    private val repository: FirebaseUserRepository
) : ViewModel() {
    private val user: MutableLiveData<User> = MutableLiveData()

    companion object {
        private val TAG: String = UserViewModel::class.java.name
    }

    init {
        readUserDetailsFromFireStore()
    }

    fun getUserDetails(): LiveData<User?> = user

    private fun readUserDetailsFromFireStore() =
        repository.readUserDetailsFromFireStore().observeForever { response ->
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
        repository.setProfilePic(context, user.value!!, data)
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
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        repository.updateUserDetailsToFireStore(userDefault)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        user.value = response.value as User
                        result.value = Success(response.value)
                        context.ShowToast("User details updated!")
                    }
                    is Failure -> {
                        context.ShowToast("Failed to update user details!")
                        Log.e(TAG, response.e.message.toString())
                        result.value = Failure(response.e)
                    }
                }
            }
        return result
    }

    fun logout(): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        repository.logout()
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