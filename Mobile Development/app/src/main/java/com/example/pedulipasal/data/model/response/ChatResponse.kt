package com.example.pedulipasal.data.model.response

import android.os.Parcel
import android.os.Parcelable
import java.util.Date



data class ChatResponse(
    val chatId: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val createdAt: Date? = null,
    val updateAt: Date? = null,
    val messages: List<Message>?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        mutableListOf<Message>().apply {
            parcel.readTypedList(this, Message)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(userId)
        parcel.writeString(title)
        createdAt?.let { parcel.writeLong(it.time) }
        updateAt?.let { parcel.writeLong(it.time) }
        parcel.writeTypedList(messages)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ChatResponse> {
        override fun createFromParcel(parcel: Parcel): ChatResponse = ChatResponse(parcel)
        override fun newArray(size: Int): Array<ChatResponse?> = arrayOfNulls(size)
    }
}


data class Message(
    val messageId: String,
    val isByHuman: Boolean,
    val content: String,
    val timestamp: Date
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeByte(if (isByHuman) 1 else 0)
        parcel.writeString(content)
        parcel.writeLong(timestamp.time)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message = Message(parcel)
        override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
    }
}

data class MessageResponse(
    val message: String? = null
)

