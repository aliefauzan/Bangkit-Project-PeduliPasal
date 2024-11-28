package com.example.pedulipasal.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.data.model.response.UserResponse

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserProfileData(userResponse: UserResponse)

    @Query("SELECT * FROM user")
    fun getUserProfileData(): LiveData<UserResponse>

    @Query("DELETE FROM user")
    suspend fun deleteUserProfileData()
}

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChats(chats: List<ChatItem>)

    @Query("SELECT * FROM chat")
    fun getUserChats(): LiveData<List<ChatItem>>

    @Query("DELETE FROM chat")
    suspend fun deleteChats()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChat(chat: ChatItem)

    @Query("DELETE FROM chat WHERE chatId = :chatId")
    suspend fun deleteChat(chatId: String)
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatMessagesById(chats: List<MessageItem>)

    @Query("SELECT * FROM message WHERE chatId = :chatId")
    fun getUserChats(chatId: String): LiveData<List<MessageItem>>

    @Query("DELETE FROM message WHERE chatId = :chatId")
    suspend fun deleteChatMessageById(chatId: String)
}