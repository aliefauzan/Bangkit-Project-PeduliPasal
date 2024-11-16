package com.example.pedulipasal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.Message
import com.example.pedulipasal.helper.getProfileIcon


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val messages = ArrayList<Message>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfile: ImageView = view.findViewById(R.id.iv_userProfile)
        val userMessageText: TextView = view.findViewById(R.id.tv_userMessageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (viewType == R.layout.item_message_local) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isByHuman) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.userProfile.setImageDrawable(getProfileIcon(holder.userProfile.context, message.isByHuman ?: false))
        holder.userMessageText.text = message.content
    }

    fun setMessages(messagesList: List<Message>) {
        messages.clear()
        messages.addAll(messagesList)
        notifyDataSetChanged()
    }

    fun addMessage(message: Message) {
        messages.add(message)
        Log.d("MessageAdapter", "addMessageDipanggil ${message.content}")
        notifyItemInserted(messages.size - 1)
    }
}