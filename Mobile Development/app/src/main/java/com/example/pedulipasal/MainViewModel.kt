package com.example.pedulipasal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.ui.settings.SettingsPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val cloudRepository: CloudRepository,
    private val pref: SettingsPreferences
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return cloudRepository.getSession().asLiveData()
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }
}
