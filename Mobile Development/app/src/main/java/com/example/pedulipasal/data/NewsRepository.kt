package com.example.pedulipasal.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.pedulipasal.data.api.NewsApiService
import com.example.pedulipasal.data.model.response.NewsItem
import com.example.pedulipasal.helper.Result
import retrofit2.HttpException

class NewsRepository(
    private val newsApiService: NewsApiService
) {
    fun getNews(category: String? = null): LiveData<Result<List<NewsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val listNews = ArrayList<NewsItem>()
            val client = if (category != null) newsApiService.getNews(category = category).articles else newsApiService.getNews().articles
            client?.forEach {
                if (it != null) {
                    listNews.add(it)
                }
            }
            emit(Result.Success(listNews))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    suspend fun getDailyNews(): NewsItem {
        val listNews = ArrayList<NewsItem>()
        val response = newsApiService.getDailyNews().articles
        response?.forEach {
            if (it != null) {
                listNews.add(it)
            }
        }
        return listNews[0]
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            newsApiService: NewsApiService
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(
                   newsApiService
                )
            }.also { instance = it }
    }
}