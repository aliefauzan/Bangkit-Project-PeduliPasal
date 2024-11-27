package com.example.pedulipasal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pedulipasal.data.model.response.UserResponse

@Database(entities = [UserResponse::class], version = 1, exportSchema = false)
abstract class PeduliPasalDatabase: RoomDatabase() {
    abstract fun userDao(): userDao


    companion object {
        @Volatile
        private var instance: PeduliPasalDatabase? = null
        fun getInstance(context: Context): PeduliPasalDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PeduliPasalDatabase::class.java, "PeduliPasal.db"
                ).build()
            }
    }
}