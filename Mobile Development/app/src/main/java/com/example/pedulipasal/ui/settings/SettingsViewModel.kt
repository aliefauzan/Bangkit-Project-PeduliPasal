package com.example.pedulipasal.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.user.UserModel
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: SettingsPreferences, private val cloudRepository: CloudRepository) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSettings(): LiveData<Boolean> {
        return pref.getNotificationSettings().asLiveData()
    }

    fun saveNotificationSetting(isNotificationsEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotificationsEnabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            cloudRepository.logout()
        }
    }

    fun getSession(): LiveData<UserModel> = cloudRepository.getSession().asLiveData()
    fun getUserProfileData(userId: String) = cloudRepository.getUserProfileData(userId)
}