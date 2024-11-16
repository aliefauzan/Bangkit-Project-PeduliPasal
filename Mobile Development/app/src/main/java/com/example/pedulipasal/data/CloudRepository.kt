package com.example.pedulipasal.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.pedulipasal.data.api.CloudApiService
import com.example.pedulipasal.data.api.NewsApiService
import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.data.model.Message
import com.example.pedulipasal.data.model.MessageResponse
import retrofit2.HttpException
import com.example.pedulipasal.helper.Result

class CloudRepository(
    private val cloudApiService: CloudApiService
) {

    fun createChat(chatResponse: ChatResponse): LiveData<Result<ChatResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.createChat(chatResponse)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun addMessageToChat(chatId: String, messageData: Message): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.addMessageToChat(chatId,messageData)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun getChatMessageById(chatId: String): LiveData<Result<ChatResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.getChatMessageById(chatId)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: CloudRepository? = null
        fun getInstance(
            cloudApiService: CloudApiService
        ): CloudRepository =
            instance ?: synchronized(this) {
                instance ?: CloudRepository(
                    cloudApiService
                )
            }.also { instance = it }
    }
}