package com.example.pedulipasal.page.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.user.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val cloudRepository: CloudRepository): ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            cloudRepository.saveSession(user)
        }
    }

    fun login(loginRequest: LoginRequest) = cloudRepository.login(loginRequest)
}