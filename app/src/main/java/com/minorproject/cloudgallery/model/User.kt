package com.minorproject.cloudgallery.model

data class User(
    val UserId: String = "",
    val UserName: String = "",
    val UserEmail: String = "",
    val AccountCreatedOn: String = ""
)


data class UserInfo(
    val UserAdditionalEmail: String = "",
    val UserPhoneNumber: String = "",
    val UserDOB: String = "",
    val UserAddress: String = ""
)