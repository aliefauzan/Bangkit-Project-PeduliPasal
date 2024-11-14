package com.example.pedulipasal.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.datastore.preferences.protobuf.Timestamp
import androidx.versionedparcelable.ParcelField
import java.util.Date


data class ChatResponse(
    val chatId: String? = null,
    val userId: String? = null,
    val title: String? = null,
    val createdAt: Date? = null,
    val updateAt: Date? = null,
    val messages: List<Message?>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong().let { if (it != -1L) Date(it) else null },
        parcel.readLong().let { if (it != -1L) Date(it) else null },
        parcel.createTypedArrayList(Message.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatId)
        parcel.writeString(userId)
        parcel.writeString(title)
        parcel.writeLong(createdAt?.time ?: -1L)
        parcel.writeLong(updateAt?.time ?: -1L)
        parcel.writeTypedList(messages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatResponse> {
        override fun createFromParcel(parcel: Parcel): ChatResponse {
            return ChatResponse(parcel)
        }

        override fun newArray(size: Int): Array<ChatResponse?> {
            return arrayOfNulls(size)
        }
    }
}


data class Message(
    val messageId: String? = null,
    val isByHuman: Boolean? = null,
    val content: String? = null,
    val timestamp: Date? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readLong().let { if (it != -1L) Date(it) else null }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(messageId)
        parcel.writeValue(isByHuman)
        parcel.writeString(content)
        parcel.writeLong(timestamp?.time ?: -1L)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}