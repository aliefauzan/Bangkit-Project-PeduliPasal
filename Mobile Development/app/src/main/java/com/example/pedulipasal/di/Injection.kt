package com.example.pedulipasal.di

import android.content.Context
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.NewsRepository
import com.example.pedulipasal.data.api.ApiConfig
import com.example.pedulipasal.data.user.UserPreference
import com.example.pedulipasal.ui.settings.SettingsPreferences
import com.example.pedulipasal.ui.settings.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideNewsRepository(context: Context): NewsRepository {

        // val pref = UserPreference.getInstance(context.dataStore)
//        val apiService = ApiConfig.getApiService {
//            runBlocking { pref.getSession().first().token }
//        }

        val newsApiService = ApiConfig.getNewsApiService()
        return NewsRepository.getInstance(newsApiService)
    }

    fun provideSettingsPreferences(context: Context): SettingsPreferences {
        return SettingsPreferences.getInstance(context.dataStore)
    }

    fun provideCloudRepository(context: Context): CloudRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val cloudApiService = ApiConfig.provideCloudApiService {
            runBlocking { pref.getSession().first().token }
        }
        return CloudRepository.getInstance(cloudApiService, pref)
    }
}