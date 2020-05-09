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

/**
 * Categories ViewModel class
 */
class CategoriesViewModel(
    private val context: Context,
    private val repository: FirebaseCategoriesRepository
) : ViewModel() {
    private val categories: MutableLiveData<ArrayList<Category>> = MutableLiveData()
    private val imageList: MutableLiveData<ArrayList<Image>> = MutableLiveData()
    private val totalStorageSize: MutableLiveData<Double> = MutableLiveData()
    private val progressStatus: MediatorLiveData<ProgressStatus> = MediatorLiveData()

    companion object {
        private val TAG: String = CategoriesViewModel::class.java.name

        /**
         * enum class for ProgressStatus
         */
        enum class ProgressStatus {
            SHOW_PROGRESS,
            HIDE_PROGRESS,
        }
    }

    init {
        readCategoriesFromFireStore()
    }

    //fun to get Progress status
    fun getProgressStatus(): LiveData<ProgressStatus> = progressStatus

    //fun to get all categories
    fun getCategories(): LiveData<ArrayList<Category>> = categories

    //fun to read all categories from firebase repo
    private fun readCategoriesFromFireStore() {
        progressStatus.value = ProgressStatus.SHOW_PROGRESS
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
            progressStatus.value = ProgressStatus.HIDE_PROGRESS
        }
    }

    //fun to create new categories from firebase repo
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

    //fun to add new image to category from firebase repo
    fun saveImageToFireStore(contentURI: Uri?, categoryName: String) {
        repository.saveImageToFireStore(context, contentURI, categoryName, true, "")
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

    //fun to get total size for the view to display
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

    //fun to get List of All images
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
     * fun to delete images from firebase repo
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
            link = updatedList[updatedList.size - 1].thumb_link
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
