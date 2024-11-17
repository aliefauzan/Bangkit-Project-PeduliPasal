package com.example.pedulipasal.page.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.data.user.UserModel
import kotlinx.coroutines.launch

class SignUpViewModel(private val cloudRepository: CloudRepository): ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            cloudRepository.saveSession(user)
        }
    }

    fun register(registerRequest: RegisterRequest) = cloudRepository.register(registerRequest)

}