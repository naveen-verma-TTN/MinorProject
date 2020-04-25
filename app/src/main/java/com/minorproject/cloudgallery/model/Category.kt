package com.minorproject.cloudgallery.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class Category(
    val UserID: String,
    val CategoryName: String,
    val CategoryUploadTime: Timestamp,
    val CategoryThumbLink: String?,
    val ImagesLink: ArrayList<Image>?
) : Parcelable

@Parcelize
data class Image(
    val category: String,
    val name: String,
    val size: Long,
    val uploadTime: Timestamp,
    var link: String
) : Parcelable

@Parcelize
data class TimeImage(
    val id: String,
    val category: String,
    val name: String,
    val size: Long,
    val uploadTime: LocalDate,
    var link: String
) : Parcelable