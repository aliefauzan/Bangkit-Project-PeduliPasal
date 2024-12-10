package com.example.pedulipasal.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: SettingsPreferences, private val cloudRepository: CloudRepository) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> = pref.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getNotificationSettings(): LiveData<Boolean> = pref.getNotificationSettings().asLiveData()

    fun saveNotificationSetting(isNotificationsEnabled: Boolean) {
        viewModelScope.launch {
            pref.saveNotificationSetting(isNotificationsEnabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            cloudRepository.logout()
            pref.clearSettingsPreferences()
        }
    }

    fun getSession() = cloudRepository.getSession().asLiveData()
    fun getUserProfileData(userId: String) = cloudRepository.getUserProfileData(userId)
}
