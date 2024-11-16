package com.example.pedulipasal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.ChatResponse
import com.example.pedulipasal.databinding.ItemChatLayoutBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(
    private val context: Context,
    private val chatList: List<ChatResponse>,
    private val onItemSelectedCallback: OnItemSelected
): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onItemClicked(chatId: String)
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
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvCreatedAt.text = "${context.getString(R.string.created_at)} ${dateFormat.format(chatList[position].createdAt?: dateFormat.parse("01/01/1970"))}"
            tvUpdatedAt.text = "${context.getString(R.string.updated_at)} ${dateFormat.format(chatList[position].updateAt?: dateFormat.parse("01/01/1970"))}"
        }

        holder.itemView.setOnClickListener {
            chatList[position].chatId?.let { it1 -> onItemSelectedCallback.onItemClicked(it1) }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}