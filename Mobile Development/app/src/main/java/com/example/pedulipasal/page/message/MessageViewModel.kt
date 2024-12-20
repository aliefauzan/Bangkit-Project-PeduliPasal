package com.example.pedulipasal.page.message

import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.request.AddMessageRequest

class MessageViewModel(private val cloudRepository: CloudRepository): ViewModel() {

    fun addMessageToChat(chatId: String, addMessageRequest: AddMessageRequest) = cloudRepository.addMessageToChat(chatId, addMessageRequest)

    fun getChatMessageById(chatId: String) = cloudRepository.getChatMessageById(chatId)

    fun getGeminiAiResponse(prompt: String) = cloudRepository.getGeminiAiResponse(prompt)
}