package com.example.pedulipasal.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pedulipasal.MainViewModel
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.NewsRepository
import com.example.pedulipasal.di.Injection
import com.example.pedulipasal.page.login.LoginViewModel
import com.example.pedulipasal.page.message.MessageViewModel
import com.example.pedulipasal.page.signup.SignUpViewModel
import com.example.pedulipasal.ui.chat.ChatViewModel
import com.example.pedulipasal.ui.news.NewsViewModel
import com.example.pedulipasal.ui.quiz.QuizViewModel
import com.example.pedulipasal.ui.settings.SettingsPreferences
import com.example.pedulipasal.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val newsRepository: NewsRepository,
    private val settingsPreferences: SettingsPreferences,
    private val cloudRepository: CloudRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(newsRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(settingsPreferences, cloudRepository) as T
            }
            modelClass.isAssignableFrom(MessageViewModel::class.java) -> {
                MessageViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                // Now we pass settingsPreferences as well
                MainViewModel(cloudRepository, settingsPreferences) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(cloudRepository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(cloudRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                val instance = ViewModelFactory(
                    Injection.provideNewsRepository(context),
                    Injection.provideSettingsPreferences(context),
                    Injection.provideCloudRepository(context)
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
