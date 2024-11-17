package com.example.pedulipasal.page.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.user.UserModel
import kotlinx.coroutines.launch

class ProfileViewModel(private val cloudRepository: CloudRepository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            cloudRepository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> = cloudRepository.getSession().asLiveData()
    fun getUserProfileData(userId: String) = cloudRepository.getUserProfileData(userId)
}