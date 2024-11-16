package com.example.pedulipasal.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.pedulipasal.data.api.CloudApiService
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.data.model.response.ChatResponse
import com.example.pedulipasal.data.model.response.LoginResponse
import com.example.pedulipasal.data.model.response.Message
import com.example.pedulipasal.data.model.response.MessageResponse
import com.example.pedulipasal.data.model.response.RegisterResponse
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.data.user.UserPreference
import com.example.pedulipasal.helper.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class CloudRepository(
    private val cloudApiService: CloudApiService,
    private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun login(loginRequest: LoginRequest): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.login(loginRequest)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun register(registerRequest: RegisterRequest): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.register(registerRequest)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }


    fun createChat(token: String, createChatRequest: CreateChatRequest): LiveData<Result<ChatResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.createChat(token, createChatRequest)
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
            cloudApiService: CloudApiService,
            userPreference: UserPreference
        ): CloudRepository =
            instance ?: synchronized(this) {
                instance ?: CloudRepository(
                    cloudApiService,
                    userPreference
                )
            }.also { instance = it }
    }
}