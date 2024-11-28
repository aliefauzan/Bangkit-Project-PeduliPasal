package com.example.pedulipasal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.data.model.response.UserResponse

@Database(entities = [UserResponse::class, ChatItem::class, MessageItem::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class PeduliPasalDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

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