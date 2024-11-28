package com.example.pedulipasal.data.model.response

import androidx.room.Entity
import androidx.room.ForeignKey
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

@Entity(tableName = "message")
data class MessageItem (
    @PrimaryKey(autoGenerate = false)
    var messageId: String,
    var isHuman: Boolean,
    var content: String,
    var timestamp: Date? = null,
    var chatId: String,
    @Ignore
    var aiMessageId: String? = null,
    @Ignore
    var userMessageId: String? = null
) {
    constructor() : this(messageId = "", isHuman = false, content = "", timestamp = null, chatId = "")
}

data class MessageResponse(
    val message: String? = null,
    val aiMessage: AiResponse? = null
)

data class AiResponse (
    val isHuman: Boolean? = null,
    val content: String? = null
)

