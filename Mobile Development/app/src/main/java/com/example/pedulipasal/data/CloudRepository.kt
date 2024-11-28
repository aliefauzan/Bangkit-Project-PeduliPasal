package com.example.pedulipasal.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.pedulipasal.BuildConfig
import com.example.pedulipasal.data.api.CloudApiService
import com.example.pedulipasal.data.database.ChatDao
import com.example.pedulipasal.data.database.MessageDao
import com.example.pedulipasal.data.database.UserDao
import com.example.pedulipasal.data.model.request.AddMessageRequest
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.data.model.response.DeleteResponse
import com.example.pedulipasal.data.model.response.LoginResponse
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.data.model.response.MessageResponse
import com.example.pedulipasal.data.model.response.RegisterResponse
import com.example.pedulipasal.data.model.response.UserResponse
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.data.user.UserPreference
import com.example.pedulipasal.helper.Result
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class CloudRepository(
    private val cloudApiService: CloudApiService,
    private val userPreference: UserPreference,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao
) {

    // Room dao
    fun getUserChatHistory(userId: String): LiveData<Result<List<ChatItem>>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.getUserChatHistory(userId).chats
            chatDao.deleteChats()
            chatDao.insertChats(client)
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
        val localData: LiveData<List<ChatItem>> = chatDao.getUserChats()
        emitSource(localData.map { Result.Success(it) })
    }

    fun getUserProfileData(userId: String): LiveData<Result<UserResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.getUserProfileData(userId)
            userDao.deleteUserProfileData()
            userDao.insertUserProfileData(client)
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
        val localData: LiveData<UserResponse> = userDao.getUserProfileData()


        emitSource(localData.map { Result.Success(it) })
    }

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


    fun createChat(createChatRequest: CreateChatRequest): LiveData<Result<ChatItem>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.createChat(createChatRequest)
            emit(Result.Success(client))
            val chatItem = ChatItem(
                chatId = client.chatId,
                userId = client.userId,
                title = client.title,
                createdAt = client.createdAt,
                updatedAt = client.updatedAt
            )
            chatDao.insertChat(chatItem)
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun addMessageToChat(chatId: String, addMessageRequest: AddMessageRequest): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.addMessageToChat(chatId, addMessageRequest)
            emit(Result.Success(client))
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    // Room dao
    fun getChatMessageById(chatId: String): LiveData<Result<List<MessageItem>>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.getChatMessageById(chatId).messages
            if (client != null) {
                client.forEach {
                    it.chatId = chatId
                }
                messageDao.deleteChatMessageById(chatId)
                messageDao.insertChatMessagesById(client)
            }
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
        val localdata: LiveData<Result<List<MessageItem>>> = messageDao.getUserChats(chatId).map { Result.Success(it) }
        emitSource(localdata)
    }

    fun deleteChat(chatId: String): LiveData<Result<DeleteResponse>> = liveData {
        emit(Result.Loading)
        try {
            val client = cloudApiService.deleteChat(chatId)
            emit(Result.Success(client))
            chatDao.deleteChat(chatId)
            messageDao.deleteChatMessageById(chatId)
        } catch (e: HttpException) {
            emit(Result.Error(e.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    fun getGeminiAiResponse(prompt: String): LiveData<Result<GenerateContentResponse>> = liveData {
        emit(Result.Loading)
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
            emit(Result.Success(generativeModel.generateContent(prompt)))
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
            userPreference: UserPreference,
            userDao: UserDao,
            chatDao: ChatDao,
            messageDao: MessageDao
        ): CloudRepository =
            instance ?: synchronized(this) {
                instance ?: CloudRepository(
                    cloudApiService,
                    userPreference,
                    userDao,
                    chatDao,
                    messageDao
                )
            }.also { instance = it }
    }
}