@file:Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")

package com.minorProject.cloudGallery.model.repo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minorProject.cloudGallery.R
import com.minorProject.cloudGallery.model.bean.Category
import com.minorProject.cloudGallery.model.bean.Image
import com.minorProject.cloudGallery.model.repo.helper.Compress
import com.minorProject.cloudGallery.util.HelperClass.ShowToast

/**
 * Firebase Categories Helper class -- for network call
 */
object FirebaseCategoriesDatabaseHelper : FirebaseCategoriesRepository {
    private val TAG: String = FirebaseCategoriesDatabaseHelper::class.java.name

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var firebaseFireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var notificationManager: NotificationManagerCompat
    private const val channelId = "Progress Notification"
    private lateinit var notification: NotificationCompat.Builder

    init {
        storage = FirebaseStorage.getInstance()
    }

    /**
     * fun to read all categories from firebase
     */
    override fun readCategoriesFromFireStore(): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        val categoryList: ArrayList<Category> = ArrayList()
        firebaseFireStore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        firebaseFireStore.collection("Categories")
            .whereEqualTo("UserId", mAuth.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val map: Map<String, Any> =
                        document.data

                    var category: Category

                    category = Category(
                        UserID = map["UserId"] as String,
                        CategoryName = map["CategoryName"] as String,
                        CategoryUploadTime = map["CategoryUploadTime"] as Timestamp,
                        CategoryThumbLink = map["CategoryThumbLink"] as String,

                        // convert HashMap to ArrayList<Image>
                        ImagesList = if (map["ListImage"] != null) {
                            val imageHashMap: List<HashMap<String, Image>> =
                                map["ListImage"] as List<HashMap<String, Image>>
                            val imageList: ArrayList<Image> =
                                convertHashMapToListOfImages(imageHashMap)
                            imageList
                        } else {
                            null
                        }
                    )
                    categoryList.add(category)
                }
                result.value = Success(categoryList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
                result.value = Failure(exception)
            }

        return result
    }

    /**
     * function to convert HashMap to ArrayList<Image>
     */
    private fun convertHashMapToListOfImages(listHashMap: List<HashMap<String, Image>>): ArrayList<Image> {
        val list: ArrayList<Image> = ArrayList()
        for (item in listHashMap) {
            list.add(
                Image(
                    category = item["category"].toString(),
                    name = item["name"].toString(),
                    size = item["size"].toString().toDouble(),
                    uploadTime = item["uploadTime"] as Timestamp,
                    link = item["link"].toString(),
                    thumb_link = item["thumb_link"].toString()
                )
            )
        }
        return list
    }

    /**
     * function to create new Category
     */
    override fun createCategory(context: Context, categoryName: String): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        val documentId = "${mAuth.uid}_$categoryName"

        val category = Category(
            mAuth.uid!!,
            categoryName,
            Timestamp.now(),
            context.getString(R.string.default_category_link),
            null
        )

        val categoryHashMap = HashMap<String, Any>()

        categoryHashMap["UserId"] = category.UserID
        categoryHashMap["CategoryName"] = category.CategoryName
        categoryHashMap["CategoryUploadTime"] = category.CategoryUploadTime
        categoryHashMap["CategoryThumbLink"] = category.CategoryThumbLink

        firebaseFireStore = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference =
            firebaseFireStore.collection("Categories").document(documentId)
        docIdRef.set(categoryHashMap)
            .addOnSuccessListener {
                result.value = Success(category)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                result.value = Failure(e)
            }
        return result
    }

    /**
     * function to save image on FireBase Storage and and send the image's information
     * to fireStore cloud database.
     */
    override fun saveImageToFireStore(
        context: Context,
        filePath: Uri?,
        categoryName: String,
        compress: Boolean,
        thumbLink: String
    ): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        if (filePath != null) {
            storageReference = storage?.reference
            var uriPath: Uri? = filePath
            val path: String
            val filename: String
            if (compress) {
                filename = "thumb_" + "${System.currentTimeMillis()}.jpg"
                path = "${mAuth.uid}/Category/" + categoryName + "/thumb/" + filename
                uriPath = Compress.getThumbnail(filePath, context)
            } else {
                filename = "image_" + "${System.currentTimeMillis()}.jpg"
                path = "${mAuth.uid}/Category/" + categoryName + "/" + filename
                sendNotification(context, filename)
                notificationManager.notify(1001, notification.build())
            }

            val ref = storageReference!!.child(path)

            ref.putFile(uriPath!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener { link ->
                            ref.metadata.addOnSuccessListener { metaData ->
                                /**
                                 * upload thumbnail and then call the same method to upload
                                 * image in full resolution
                                 */
                                if (compress) {
                                    saveImageToFireStore(
                                        context,
                                        filePath,
                                        categoryName,
                                        false,
                                        link.toString()
                                    ).observeForever { response ->
                                        when (response) {
                                            is Success -> {
                                                result.value = response
                                            }
                                            is Failure -> {
                                                context.ShowToast("Failed to Save thumb Image!")
                                                Log.e(TAG, response.e.message.toString())
                                            }
                                        }
                                    }
                                } else {
                                    // update the original image and thumbnail link
                                    val image = Image(
                                        categoryName,
                                        filename,
                                        metaData.sizeBytes.toDouble(),
                                        Timestamp.now(),
                                        link.toString(),
                                        thumbLink
                                    )
                                    val docIdRef: DocumentReference =
                                        firebaseFireStore.collection("Categories")
                                            .document(mAuth.uid!! + "_" + categoryName)
                                    docIdRef.get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val document = task.result
                                            if (document!!.exists()) {
                                                docIdRef.update(
                                                    "ListImage",
                                                    FieldValue.arrayUnion(image),
                                                    "CategoryThumbLink",
                                                    thumbLink
                                                )
                                                    .addOnSuccessListener {
                                                        notification.setContentText("Uploading Completed")
                                                            .setOngoing(false)

                                                        notificationManager.notify(
                                                            1001,
                                                            notification.build()
                                                        )
                                                        notificationManager.cancel(1001)

                                                        result.value = Success(image)
                                                    }
                                                    .addOnFailureListener { e ->
                                                        result.value = Failure(e)
                                                    }
                                            } else {
                                                result.value = Failure(Exception(task.exception))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        result.value =
                            Failure(Exception("Error happened during the upload process"))
                    }
                }.addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                    Log.e("Progress", progress.toString())
                    if (!compress) {
                        notification.setContentText(progress.toInt().toString() + " %")
                            .setProgress(100, progress.toInt(), false)
                            .setOngoing(false)
                        notificationManager.notify(1001, notification.build())
                    }

                }.addOnFailureListener { e ->
                    result.value = Failure(e)
                }
        } else {
            result.value = Failure(Exception("Something went wrong!"))
        }
        return result
    }

    /**
     * setup the notification while uploading images
     */
    private fun sendNotification(context: Context, fileName: String) {
        notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(
                    channelId,
                    "Progress Upload Images Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            mChannel.description = "Progress Upload Images Notification Channel"
            mChannel.enableLights(true)
            val manager = getSystemService(
                context,
                NotificationManager::class.java
            )
            manager!!.createNotificationChannel(mChannel)
        }
        notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(fileName)
            .setContentText("Uploading")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, true)
            .setAutoCancel(true)
    }

    /**
     * delete the image data from fireStore cloud database
     */
    override fun deleteImagesFromFirebase(
        image: Image,
        link: String?
    ): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        val rootRef = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference =
            rootRef.collection("Categories")
                .document(mAuth.uid!! + "_" + image.category)
        docIdRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    docIdRef.update(
                        "ListImage",
                        FieldValue.arrayRemove(image),
                        "CategoryThumbLink",
                        link
                    )
                        .addOnSuccessListener {
                            result.value = Success(true)
                        }
                        .addOnFailureListener { e ->
                            result.value = Failure(e)
                        }
                } else {
                    result.value = Failure(Exception(task.exception))
                }
            }
        }
        return result
    }

    /**
     * delete category from fireStore cloud database
     */
    override fun deleteCategoryFromFirebase(categoryId: String): LiveData<Result<Any?>> {
        val result: MutableLiveData<Result<Any?>> = MutableLiveData()
        val rootRef = FirebaseFirestore.getInstance()
        rootRef.collection("Categories").document(categoryId)
            .delete()
            .addOnSuccessListener {
                result.value = Success(true)
            }
            .addOnFailureListener { e ->
                result.value = Failure(Exception(e))
            }
        return result
    }
}