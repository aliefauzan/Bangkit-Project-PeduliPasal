package com.example.pedulipasal.page.chat

import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.ChatResponse

class ChatViewModel(private val cloudRepository: CloudRepository): ViewModel() {
    fun createChat(chatResponse: ChatResponse) = cloudRepository.createChat(chatResponse)

}