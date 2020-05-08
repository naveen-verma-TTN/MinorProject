package com.minorProject.cloudGallery.model.bean

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

/**
 * Category Model class
 */
@Parcelize
data class Category(
    val UserID: String,
    val CategoryName: String,
    val CategoryUploadTime: Timestamp,
    var CategoryThumbLink: String,
    val ImagesList: ArrayList<Image>?
) : Parcelable

/**
 * Image Model class
 */
@Parcelize
data class Image(
    val category: String,
    val name: String,
    val size: Double,
    val uploadTime: Timestamp,
    var link: String
) : Parcelable
