package com.example.pedulipasal.data.model.response

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date


data class HistoryChatResponse(
    val chats: List<ChatItem>
)

@Entity(tableName = "chat")
data class ChatItem(
    @PrimaryKey(autoGenerate = false)
    var chatId: String,
    var userId: String? = null,
    var title: String? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    @Ignore
    var messages: List<MessageItem>?=null
) {
    constructor() : this(chatId = "", userId = null, title = null, createdAt = null, updatedAt = null)
}

data class MessageItem (
    val isHuman: Boolean,
    val content: String,
    val timestamp: Date? = null
)

data class MessageResponse(
    val message: String? = null,
    val aiMessage: AiResponse? = null
)

data class AiResponse (
    val isHuman: Boolean? = null,
    val content: String? = null
)

