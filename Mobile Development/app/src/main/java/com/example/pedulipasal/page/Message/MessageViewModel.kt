package com.example.pedulipasal.page.Message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.response.MessageItem

class MessageViewModel(private val cloudRepository: CloudRepository): ViewModel() {

    fun addMessageToChat(chatId: String, messageItemData: MessageItem) = cloudRepository.addMessageToChat(chatId, messageItemData)

    fun getChatMessageById(chatId: String) = cloudRepository.getChatMessageById(chatId)

}