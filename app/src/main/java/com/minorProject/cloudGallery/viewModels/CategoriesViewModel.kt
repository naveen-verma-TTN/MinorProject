@file:Suppress("UNCHECKED_CAST")

package com.minorProject.cloudGallery.viewModels

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.model.bean.Image
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseCategoriesRepository
import com.minorProject.cloudGallery.model.repo.Result
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.ShowToast

@RequiresApi(Build.VERSION_CODES.O)
class CategoriesViewModel(
    private val context: Context,
    private val repository: FirebaseCategoriesRepository
) : ViewModel() {
    private val categories: MutableLiveData<ArrayList<Category>> = MutableLiveData()
    private val imageList: MutableLiveData<ArrayList<Image>> = MutableLiveData()

    private val totalStorageSize: MutableLiveData<Double> = MutableLiveData()

    companion object {
        private val TAG: String = CategoriesViewModel::class.java.name
    }

    init {
        readCategoriesFromFireStore()
    }

    fun getCategories(): LiveData<ArrayList<Category>> = categories

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readCategoriesFromFireStore() {
        repository.readCategoriesFromFireStore().observeForever { response ->
            when (response) {
                is Success -> {
                    categories.value = response.value as ArrayList<Category>
                    totalStorageSize.value = getTotalStorageSize()
                    imageList.value = generateImageList()
                }
                is Failure -> {
                    Log.e(TAG, response.e.message.toString())
                }
            }
        }
    }

    fun createCategory(categoryName: String) {
        repository.createCategory(context, categoryName)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        context.ShowToast("Category Successfully Created!")
                        val categoriesList = categories.value
                        categoriesList?.add(response.value as Category)
                        categories.value = categoriesList
                    }
                    is Failure -> {
                        context.ShowToast("Failed to Created Category!")
                        Log.e(TAG, response.e.message.toString())
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImageToFireStore(contentURI: Uri?, categoryName: String) {
        repository.saveImageToFireStore(context, contentURI, categoryName)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        readCategoriesFromFireStore()
                    }
                    is Failure -> {
                        context.ShowToast("Failed to Save Image!")
                        Log.e(TAG, response.e.message.toString())
                    }
                }
            }
    }

    fun getTotalSize(): LiveData<Double> = totalStorageSize

    /**
     * fun to get total Storage space size --- for specific user
     */
    private fun getTotalStorageSize(): Double {
        var size = 0.0
        categories.value?.forEach { item ->
            if (item.ImagesList != null) {
                item.ImagesList.forEach { image ->
                    size += (image.size / (1024 * 1024))
                }
            }
        }
        return size
    }

    fun getImageList(): LiveData<ArrayList<Image>> = imageList

    /**
     * fun to getAll Images from Category list
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateImageList(): ArrayList<Image> {
        val list: ArrayList<Image> = ArrayList()
        categories.value?.forEach { item ->
            if (item.ImagesList != null) {
                item.ImagesList.forEach { image ->
                    list.add(image)
                }
            }
        }
        sortList(list)
        return list
    }

    /**
     * fun to sort the image list according to the upload time
     * <Images uploaded recently will be on top of the list followed by other images>
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sortList(imageList: ArrayList<Image>) {
        imageList.sortWith(Comparator { o1: Image, o2: Image ->
            if (o1.uploadTime.toDate().after(o2.uploadTime.toDate())) {
                -1
            } else {
                1
            }
        })
    }

    /**
     * fun to delete images from firebase cloud database
     */
    fun deleteImagesFromFirebase(
        image: ArrayList<Image>,
        deletionPosition: androidx.recyclerview.selection.Selection<Long>
    ): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        val deletionList = ArrayList<Image>()
        deletionPosition.forEach { item ->
            deletionList.add(image[item.toInt()])
        }

        val updatedList = image.asIterable().minus(deletionList)

        var link: String = context.getString(R.string.default_category_link)
        if (updatedList.isNotEmpty()) {
            link = updatedList[updatedList.size - 1].link
        }

        deletionList.forEach { dImage ->
            repository.deleteImagesFromFirebase(dImage, link)
                .observeForever { response ->
                    when (response) {
                        is Success -> {
                            readCategoriesFromFireStore()
                            result.value = Success(deletionList.count())
                        }
                        is Failure -> {
                            Log.e(TAG, response.e.message.toString())
                            result.value = Failure(response.e)
                        }
                    }
                }
        }
        return result
    }
}
