package com.example.pedulipasal.data.model

import androidx.datastore.preferences.protobuf.Timestamp
import java.util.Date

data class ChatResponse(
    val chatId: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val createdAt: Date? = null,
    val updateAt: Date? = null,
    val messages: List<Message?>? = null
)

data class Message(
    val messageId: String? = null,
    val isByHuman: Boolean? = null,
    val content: String? = null,
    val timestamp: Date? = null
)