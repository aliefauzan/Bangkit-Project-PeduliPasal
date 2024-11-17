package com.example.pedulipasal.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pedulipasal.MainViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.NewsRepository
import com.example.pedulipasal.di.Injection
import com.example.pedulipasal.page.Message.MessageViewModel
import com.example.pedulipasal.page.login.LoginViewModel
import com.example.pedulipasal.page.profile.ProfileViewModel
import com.example.pedulipasal.ui.news.NewsViewModel
import com.example.pedulipasal.ui.settings.SettingsPreferences
import com.example.pedulipasal.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val newsRepository: NewsRepository,
    private val settingsPreferences: SettingsPreferences,
    private val cloudRepository: CloudRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(newsRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsPreferences) as T
            }
            modelClass.isAssignableFrom(MessageViewModel::class.java) -> {
                MessageViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(com.example.pedulipasal.ui.chat.ChatViewModel::class.java) -> {
                com.example.pedulipasal.ui.chat.ChatViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(cloudRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideNewsRepository(context),
                        Injection.provideSettingsPreferences(context),
                        Injection.provideCloudRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}