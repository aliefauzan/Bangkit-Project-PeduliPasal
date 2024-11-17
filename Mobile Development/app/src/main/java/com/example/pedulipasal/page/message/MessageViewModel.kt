package com.example.pedulipasal.page.message

import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.request.AddMessageRequest
import com.example.pedulipasal.data.model.response.MessageItem

class MessageViewModel(private val cloudRepository: CloudRepository): ViewModel() {

    fun addMessageToChat(chatId: String, addMessageRequest: AddMessageRequest) = cloudRepository.addMessageToChat(chatId, addMessageRequest)

    fun getChatMessageById(chatId: String) = cloudRepository.getChatMessageById(chatId)

}