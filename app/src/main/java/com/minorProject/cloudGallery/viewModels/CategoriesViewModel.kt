package com.minorProject.cloudGallery.viewModels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.model.bean.Image
import com.minorProject.cloudGallery.model.repo.Failure
import com.minorProject.cloudGallery.model.repo.FirebaseCategoriesDatabaseHelper
import com.minorProject.cloudGallery.model.repo.Success
import com.minorProject.cloudGallery.util.HelperClass.ShowToast

@RequiresApi(Build.VERSION_CODES.O)
class CategoriesViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
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
    private fun readCategoriesFromFireStore() =
        FirebaseCategoriesDatabaseHelper.readCategoriesFromFireStore().observeForever { response ->
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

    fun createCategory(categoryName: String) {
        FirebaseCategoriesDatabaseHelper.createCategory(context, categoryName)
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
        FirebaseCategoriesDatabaseHelper.saveImageToFireStore(context, contentURI, categoryName)
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
        image: Image,
        link: String?
    ) {
        FirebaseCategoriesDatabaseHelper.deleteImagesFromFirebase(image,link)
            .observeForever { response ->
                when (response) {
                    is Success -> {
                        readCategoriesFromFireStore()
                    }
                    is Failure -> {
                        context.ShowToast("Failed to delete Image!")
                        Log.e(TAG, response.e.message.toString())
                    }
                }
            }
    }
}