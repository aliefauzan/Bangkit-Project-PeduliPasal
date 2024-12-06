package com.example.pedulipasal.data.api

import com.example.pedulipasal.BuildConfig
import com.example.pedulipasal.data.model.response.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getNews(
        @Query("category") category: String = null ?: "general",
        @Query("country") country: String = null ?: "us",
        @Query("language") language: String = null ?: "en",
        @Query("apiKey") apiKey: String = null ?: BuildConfig.NEWS_API_KEY
    ): NewsResponse

    @GET("top-headlines")
    suspend fun getDailyNews(
        @Query("country") country: String = null ?: "us",
        @Query("language") language: String = null ?: "en",
        @Query("pageSize") pageSize: Int = null ?: 1,
        @Query("apiKey") apiKey: String = null ?: BuildConfig.NEWS_API_KEY
    ): NewsResponse

}