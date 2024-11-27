package com.example.pedulipasal.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pedulipasal.data.model.response.UserResponse

@Dao
interface userDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfileData(userResponse: UserResponse)

    @Query("SELECT * FROM user")
    fun getUserProfileData(): LiveData<UserResponse>

    @Query("DELETE FROM user")
    suspend fun deleteUserProfileData()
}