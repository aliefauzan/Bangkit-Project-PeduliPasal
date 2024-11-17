package com.example.pedulipasal.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.user.UserModel

class ChatViewModel(private val cloudRepository: CloudRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return cloudRepository.getSession().asLiveData()
    }
}