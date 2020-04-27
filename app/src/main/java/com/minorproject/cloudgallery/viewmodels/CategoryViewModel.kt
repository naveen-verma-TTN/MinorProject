package com.minorproject.cloudgallery.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minorproject.cloudgallery.R
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image


@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
@RequiresApi(Build.VERSION_CODES.O)
class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    val allCategories: MutableLiveData<ArrayList<Category>> = MutableLiveData()
    private var list: ArrayList<Category> = ArrayList()
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var notificationManager: NotificationManagerCompat
    private val channelId = "Progress Notification"
    private lateinit var notification: NotificationCompat.Builder

    private val context = getApplication<Application>().applicationContext


    companion object {
        private const val TAG: String = "CategoryViewModel"
    }

    private var mAuth: FirebaseAuth? = null

    init {
        mAuth = FirebaseAuth.getInstance()
        readCategoriesFromFireStore()
    }


    private fun readCategoriesFromFireStore() {
        val fireStore = FirebaseFirestore.getInstance()
        fireStore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        list.clear()
        fireStore.collection("Categories")
            .whereEqualTo("UserId", mAuth?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val map: Map<String, Any> =
                        document.data

                    var category: Category


                    if (map["ListImage"] != null) {
                        val imageHashMap: List<HashMap<String, Image>> =
                            map["ListImage"] as List<HashMap<String, Image>>
                        val imageList: ArrayList<Image> = convertHashMapToListOfImages(imageHashMap)

                        category = Category(
                            UserID = map["UserId"] as String,
                            CategoryName = map["CategoryName"] as String,
                            CategoryUploadTime = map["CategoryUploadTime"] as Timestamp,
                            CategoryThumbLink = map["CategoryThumbLink"] as String?,
                            ImagesList = imageList
                        )
                    } else {
                        category = Category(
                            UserID = map["UserId"] as String,
                            CategoryName = map["CategoryName"] as String,
                            CategoryUploadTime = map["CategoryUploadTime"] as Timestamp,
                            CategoryThumbLink = map["CategoryThumbLink"] as String?,
                            ImagesList = null
                        )
                    }
                    list.add(category)
                }
                allCategories.value = list
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    private fun convertHashMapToListOfImages(listHashMap: List<HashMap<String, Image>>): ArrayList<Image> {
        val list: ArrayList<Image> = ArrayList()
        for (item in listHashMap) {
            list.add(
                Image(
                    category = item["category"].toString(),
                    name = item["name"].toString(),
                    size = item["size"].toString().toLong(),
                    uploadTime = item["uploadTime"] as Timestamp,
                    link = item["link"].toString()
                )
            )
        }
        return list
    }


    fun insertCategory(
        categoryName: String
    ) {
        val id = "${mAuth?.uid}_$categoryName"

        val category = Category(
            mAuth?.uid!!,
            categoryName,
            Timestamp.now(),
            null,
            null
        )

        val categoryHashMap = HashMap<String, Any>()

        categoryHashMap["UserId"] = category.UserID
        categoryHashMap["CategoryName"] = category.CategoryName
        categoryHashMap["CategoryUploadTime"] = category.CategoryUploadTime

        val rootRef = FirebaseFirestore.getInstance()
        val docIdRef: DocumentReference =
            rootRef.collection("Categories").document(id)
        docIdRef.set(categoryHashMap)
            .addOnSuccessListener {
                Toast.makeText(
                    getApplication(),
                    "$categoryName is successfully inserted",
                    Toast.LENGTH_LONG
                ).show()
                list.add(category)
                allCategories.value = list
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun saveImageToFireStore(filePath: Uri?, categoryName: String) {
        var downloadUri: String?
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        val filename = "image_" + "${System.currentTimeMillis()}.jpg"
        val path =
            "${mAuth?.uid}/Category/" + categoryName + "/" + filename
        if (filePath != null) {
            val ref = storageReference!!.child(path)
            sendNotification(filename)
            notificationManager.notify(1, notification.build())
            ref.putFile(filePath)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.downloadUrl.addOnSuccessListener { link ->
                            downloadUri = link.toString()
                            ref.metadata.addOnSuccessListener { metaData ->

                                val image = Image(
                                    categoryName,
                                    filename,
                                    metaData.sizeBytes,
                                    Timestamp.now(),
                                    downloadUri!!
                                )

                                val rootRef = FirebaseFirestore.getInstance()
                                val docIdRef: DocumentReference =
                                    rootRef.collection("Categories")
                                        .document(mAuth?.uid!! + "_" + categoryName)
                                docIdRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document!!.exists()) {
                                            docIdRef.update(
                                                "ListImage",
                                                FieldValue.arrayUnion(image),
                                                "CategoryThumbLink",
                                                downloadUri
                                            )
                                                .addOnSuccessListener {
                                                    readCategoriesFromFireStore()

                                                    notification.setContentText("Uploading Completed")
                                                        .setOngoing(false)

                                                    notificationManager.notify(
                                                        1,
                                                        notification.build()
                                                    )
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot successfully written!"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(TAG, "Error writing document", e)
                                                }
                                        } else {
                                            Log.d(TAG, "Failed with: ", task.exception)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(
                            TAG,
                            "Error happened during the upload process"
                        )
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
                    Log.e("Uploaded", "failed: ${e.message}")
                }
        }
    }

    private fun sendNotification(fileName: String) {
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
}