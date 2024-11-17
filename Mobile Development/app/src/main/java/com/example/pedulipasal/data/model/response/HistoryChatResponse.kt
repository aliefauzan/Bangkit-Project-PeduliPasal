package com.example.pedulipasal.data.model.response

import android.os.Parcel
import android.os.Parcelable
import java.util.Date


data class HistoryChatResponse(
    val chats: List<ChatItem>
)


data class ChatItem(
    val chatId: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val createdAt: Date? = null,
    val updateAt: Date? = null,
    val messageItems: List<MessageItem>?=null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        mutableListOf<MessageItem>().apply {
            parcel.readTypedList(this, MessageItem)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(userId)
        parcel.writeString(title)
        createdAt?.let { parcel.writeLong(it.time) }
        updateAt?.let { parcel.writeLong(it.time) }
        parcel.writeTypedList(messageItems)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ChatItem> {
        override fun createFromParcel(parcel: Parcel): ChatItem = ChatItem(parcel)
        override fun newArray(size: Int): Array<ChatItem?> = arrayOfNulls(size)
    }
}


data class MessageItem (
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

    companion object CREATOR : Parcelable.Creator<MessageItem> {
        override fun createFromParcel(parcel: Parcel): MessageItem = MessageItem(parcel)
        override fun newArray(size: Int): Array<MessageItem?> = arrayOfNulls(size)
    }
}

data class MessageResponse(
    val message: String? = null
)

