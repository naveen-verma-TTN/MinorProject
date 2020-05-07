package com.minorProject.cloudGallery.model.repo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.minorProject.cloudGallery.model.bean.User

interface FirebaseUserRepository {
    fun readUserDetailsFromFireStore(): LiveData<Result<Any?>>
    fun setProfilePic(context: Context, user: User, uri: Uri): LiveData<Result<Any?>>
    fun updateUserDetailsToFireStore(user: User): LiveData<Result<Any?>>
    fun logout(): LiveData<Result<Any?>>
}