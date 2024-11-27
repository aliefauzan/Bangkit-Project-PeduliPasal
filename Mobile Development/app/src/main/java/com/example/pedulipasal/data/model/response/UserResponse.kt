package com.example.pedulipasal.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)