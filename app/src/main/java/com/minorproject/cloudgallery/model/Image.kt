package com.minorproject.cloudgallery.model

import java.time.LocalDate

data class Image(
    val id: String,
    val category: String,
    val name: String,
    val size: Long,
    val uploadTime: LocalDate,
    val link: String
)