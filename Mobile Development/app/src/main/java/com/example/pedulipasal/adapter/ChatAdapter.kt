package com.example.pedulipasal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.databinding.ItemChatLayoutBinding
import com.example.pedulipasal.helper.getDateFormat
import com.example.pedulipasal.helper.getTimeFormat
import java.util.Date

class ChatAdapter(
    private val context: Context,
    private var chatList: List<ChatItem>,
    private val onItemSelectedCallback: OnItemSelected
): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onItemClicked(chatId: String)
        fun onButtonDeleteClick(chatId: String)
    }

    class ViewHolder(val binding: ItemChatLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvTitle.text = chatList[position].title
            val currentDate = getDateFormat( Date())
            Log.d("ChatAdapter", "Current Date item $position $currentDate")
            Log.d("ChatAdapter", "Created Date $position ${chatList[position].createdAt}")
            Log.d("ChatAdapter", "Updated Date $position ${chatList[position].updatedAt}")

            val createdAt = getDateFormat(chatList[position].createdAt)
            val updatedAt = getDateFormat(chatList[position].updatedAt)

            if (createdAt == currentDate) {
                tvCreatedAt.text = "${context.getString(R.string.created_at)} ${getTimeFormat(chatList[position].createdAt)}"
            } else {
                tvCreatedAt.text = "${context.getString(R.string.created_at)} ${getDateFormat(chatList[position].createdAt)}"
            }
            if (updatedAt == currentDate) {
                tvUpdatedAt.text = "${context.getString(R.string.updated_at)} ${getTimeFormat(chatList[position].updatedAt)}"
            } else {
                tvUpdatedAt.text = "${context.getString(R.string.updated_at)} ${getDateFormat(chatList[position].updatedAt)}"
            }
        }

        holder.itemView.setOnClickListener {
            chatList[position].chatId?.let { it1 ->
                onItemSelectedCallback.onItemClicked(it1)
            }
        }
        holder.binding.btnDeleteChat.setOnClickListener {
            chatList[position].chatId?.let { it1 ->
                onItemSelectedCallback.onButtonDeleteClick(it1)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun deleteItem(chatId: String) {
        val itemPosition = chatList.indexOfFirst { it.chatId == chatId }
        if (itemPosition != -1) {
            val mutableList = chatList.toMutableList()
            mutableList.removeAt(itemPosition)
            chatList = mutableList
            notifyDataSetChanged()
        }
    }
}