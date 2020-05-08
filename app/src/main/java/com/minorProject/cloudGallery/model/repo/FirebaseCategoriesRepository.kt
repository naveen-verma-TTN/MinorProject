package com.minorProject.cloudGallery.model.repo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.minorProject.cloudGallery.model.bean.Image

/**
 * Firebase Categories Repository interface
 */
interface FirebaseCategoriesRepository {
    fun readCategoriesFromFireStore(): LiveData<Result<Any?>>
    fun createCategory(context: Context, categoryName: String): LiveData<Result<Any?>>
    fun saveImageToFireStore(
        context: Context,
        filePath: Uri?,
        categoryName: String
    ): LiveData<Result<Any?>>

    fun deleteImagesFromFirebase(image: Image, link: String?): LiveData<Result<Any?>>
}