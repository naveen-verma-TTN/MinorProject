package com.minorproject.cloudgallery.viewmodels

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.minorproject.cloudgallery.model.Category
import com.minorproject.cloudgallery.model.Image
import java.util.*
import kotlin.collections.ArrayList


@RequiresApi(Build.VERSION_CODES.O)
class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    val allCategories: MutableLiveData<ArrayList<Category>> = MutableLiveData()
    private var list: ArrayList<Category> = ArrayList()

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

        fireStore.collection("Categories")
            .whereEqualTo("UserId", mAuth?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val map: Map<String, Any> =
                        document.data

                    val category = Category(
                        UserID = map["UserId"] as String,
                        CategoryName = map["CategoryName"] as String,
                        CategoryUploadTime = map["CategoryUploadTime"] as Timestamp,
                        CategoryThumbLink = map["CategoryThumbLink"] as String?,
                        ListImage = map["ListImage"] as ArrayList<Image>?
                    )
                    list.add(category)
                }
                allCategories.value = list
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }


    fun insertCategory(
        categoryName: String
    ) {
        val id = "${mAuth?.uid}_$categoryName"

        val category = Category(
            mAuth?.uid!!,
            categoryName,
            Timestamp.now(),
            "https://pngimage.net/wp-content/uploads/2018/05/cartoon-anime-png-8.png",
            null
        )

        val categoryHashMap = HashMap<String, Any>()

        categoryHashMap["UserId"] = category.UserID
        categoryHashMap["CategoryName"] = category.CategoryName
        categoryHashMap["CategoryUploadTime"] = category.CategoryUploadTime

        categoryHashMap["CategoryThumbLink"]= category.CategoryThumbLink!!

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
}