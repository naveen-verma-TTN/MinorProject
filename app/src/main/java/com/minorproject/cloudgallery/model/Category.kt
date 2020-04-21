package com.minorproject.cloudgallery.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
@Parcelize
data class Category(
    val UserID: String,
    val CategoryName: String,
    val CategoryUploadTime: Timestamp,
    val CategoryThumbLink: String?,
    val ListImage: List<Image>?
) : Parcelable

@Parcelize
data class Image(
    val id: String,
    val category: String,
    val name: String,
    val size: Long,
    val uploadTime: LocalDate,
    val link: String
) : Parcelable