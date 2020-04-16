package com.minorproject.cloudgallery.model

data class User(
    var UserId: String = "",
    var UserName: String = "",
    var UserEmail: String = "",
    var UserPassword: String = "",
    var AccountCreatedOn: String = "",
    var UserAdditionalEmail: String = "",
    var UserPhoneNumber: String = "",
    var UserDOB: String = "",
    var UserAddress: String = ""
)
