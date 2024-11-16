package com.example.pedulipasal.page.chat

import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.response.Message

class ChatViewModel(private val cloudRepository: CloudRepository): ViewModel() {
    fun createChat(token: String, createChatRequest: CreateChatRequest) = cloudRepository.createChat(token, createChatRequest)

    fun addMessageToChat(chatId: String, messageData: Message) = cloudRepository.addMessageToChat(chatId, messageData)

    fun getChatMessageById(chatId: String) = cloudRepository.getChatMessageById(chatId)
}