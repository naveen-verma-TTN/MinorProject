@file:Suppress("UNCHECKED_CAST")

package com.minorProject.cloudGallery.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minorProject.cloudGallery.MyApplication
import com.minorProject.cloudGallery.model.repo.*
import com.minorProject.cloudGallery.viewModels.CategoriesViewModel
import com.minorProject.cloudGallery.viewModels.UserViewModel

class MyViewModelFactory : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    MyApplication.getMyInstance().applicationContext,
                    FirebaseAuthHelper as FirebaseAuthRepository
                ) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(
                    MyApplication.getMyInstance().applicationContext,
                    FirebaseUserDatabaseHelper as FirebaseUserRepository
                ) as T
            }
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> {
                CategoriesViewModel(
                    MyApplication.getMyInstance().applicationContext,
                    FirebaseCategoriesDatabaseHelper as FirebaseCategoriesRepository
                ) as T
            }
            else -> throw IllegalArgumentException("viewModel class not found")
        }
    }
}