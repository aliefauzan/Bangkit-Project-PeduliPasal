package com.example.pedulipasal.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.datastore.preferences.protobuf.Timestamp
import androidx.versionedparcelable.ParcelField
import java.util.Date



data class ChatResponse(
    val chatId: String,
    val userId: String,
    val title: String,
    val createdAt: Date,
    val updateAt: Date,
    val messages: List<Message>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        mutableListOf<Message>().apply {
            parcel.readTypedList(this, Message.CREATOR)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(userId)
        parcel.writeString(title)
        parcel.writeLong(createdAt.time)
        parcel.writeLong(updateAt.time)
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