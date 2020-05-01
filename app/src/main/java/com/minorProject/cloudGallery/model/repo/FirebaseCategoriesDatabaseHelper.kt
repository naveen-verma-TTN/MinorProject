@file:Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")

package com.minorProject.cloudGallery.model.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

object FirebaseCategoriesDatabaseHelper {
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

    fun readCategoriesFromFireStore(): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
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
                        CategoryThumbLink = map["CategoryThumbLink"] as String?,
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
                    link = item["link"].toString()
                )
            )
        }
        return list
    }

    /**
     * function to create new Category
     */
    fun createCategory(context: Context, categoryName: String): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
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
    fun saveImageToFireStore(
        context: Context,
        filePath: Uri?,
        categoryName: String
    ): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
        storageReference = storage?.reference
        val filename = "image_" + "${System.currentTimeMillis()}.jpg"
        val path =
            "${mAuth.uid}/Category/" + categoryName + "/" + filename
        if (filePath != null) {
            val ref = storageReference!!.child(path)
            sendNotification(context, filename)
            notificationManager.notify(1, notification.build())
            ref.putFile(filePath)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener { link ->
                            ref.metadata.addOnSuccessListener { metaData ->
                                val image =
                                    Image(
                                        categoryName,
                                        filename,
                                        metaData.sizeBytes.toDouble(),
                                        Timestamp.now(),
                                        link.toString()
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
                                                link.toString()
                                            )
                                                .addOnSuccessListener {
                                                    notification.setContentText("Uploading Completed")
                                                        .setOngoing(false)

                                                    notificationManager.notify(
                                                        1,
                                                        notification.build()
                                                    )
                                                    notificationManager.cancel(1)

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
                    } else {
                        result.value =
                            Failure(Exception("Error happened during the upload process"))
                    }
                }.addOnProgressListener { taskSnapshot ->
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                    notification.setContentText(progress.toInt().toString() + " %")
                        .setProgress(100, progress.toInt(), false)
                        .setOngoing(false)

                    notificationManager.notify(1, notification.build())
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
    fun deleteImagesFromFirebase(image: Image): LiveData<Result<Any?>> {
        val result: MediatorLiveData<Result<Any?>> = MediatorLiveData()
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
                        FieldValue.arrayRemove(image)
                    )
                        .addOnSuccessListener {
                            readCategoriesFromFireStore()
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
}