package com.minorProject.cloudGallery.model.repo

sealed class Result<out T>
data class Success<out T>(val value: T) : Result<T>()
data class Failure<out T>(val e: Throwable) : Result<T>()