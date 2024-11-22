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
    val updatedAt: Date? = null,
    val messages: List<MessageItem>?=null
)

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

