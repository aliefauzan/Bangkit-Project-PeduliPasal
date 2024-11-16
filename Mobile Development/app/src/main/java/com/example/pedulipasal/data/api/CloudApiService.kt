package com.example.pedulipasal.data.api

import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.data.model.Message
import com.example.pedulipasal.data.model.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CloudApiService {
    @POST("/")
    suspend fun createChat(@Body chatResponse: ChatResponse) : ChatResponse

    @POST("/{chatId}")
    suspend fun addMessageToChat(
        @Path("chatId") chatId: String,
        @Body messageData: Message
    ): MessageResponse

    @GET("/{chatId}")
    suspend fun getChatMessageById(
        @Path("chatId") chatId: String
    ): ChatResponse

}