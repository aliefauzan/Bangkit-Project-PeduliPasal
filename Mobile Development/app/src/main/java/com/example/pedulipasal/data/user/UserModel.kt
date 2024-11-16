package com.example.pedulipasal.data.user

data class UserModel(
    val userId: String,
    val token: String,
    val isLogin: Boolean = false
)