package com.example.pedulipasal.data.model.response

import java.util.Date


data class HistoryChatResponse(
    val chats: List<ChatItem>
)

data class ChatItem(
    val chatId: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val createdAt: Date? = null,
    val updateAt: Date? = null,
    val messages: List<MessageItem>?=null
)

data class MessageItem (
    val isHuman: Boolean,
    val content: String
)

data class MessageResponse(
    val message: String? = null
)

