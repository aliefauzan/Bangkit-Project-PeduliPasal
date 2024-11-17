package com.example.pedulipasal.data.api

import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.data.model.response.HistoryChatResponse
import com.example.pedulipasal.data.model.response.LoginResponse
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.data.model.response.MessageResponse
import com.example.pedulipasal.data.model.response.RegisterResponse
import com.example.pedulipasal.data.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CloudApiService {

    @GET("users/{userId}")
    suspend fun getUserProfileData(
        @Path("userId") userId: String
    ): UserResponse

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("users/reg")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @POST("chats")
    suspend fun createChat(
        @Body createChatRequest: CreateChatRequest
    ): ChatItem

    @POST("chats/{chatId}")
    suspend fun addMessageToChat(
        @Path("chatId") chatId: String,
        @Body messageItemData: MessageItem
    ): MessageResponse

    @GET("chats/{chatId}")
    suspend fun getChatMessageById(
        @Path("chatId") chatId: String
    ): ChatItem

    @GET("users/chats/{userId}")
    suspend fun getUserChatHistory (
        @Path("userId") userId: String
    ): HistoryChatResponse
}