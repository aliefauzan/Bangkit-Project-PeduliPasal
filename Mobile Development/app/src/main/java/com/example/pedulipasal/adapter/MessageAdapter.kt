package com.example.pedulipasal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.data.model.Message
import com.example.pedulipasal.helper.getProfileIcon

class MessageAdapter: RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val messages = ArrayList<Message>()
    private lateinit var userMessageText: TextView

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val messageItemView =
            LayoutInflater.from(parent.context).inflate(viewType, parent, false) as ViewGroup
        return ViewHolder(messageItemView)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userProfile = holder.itemView.findViewById<ImageView>(R.id.iv_userProfile)
        userMessageText = holder.itemView.findViewById<TextView>(R.id.tv_userMessageText)
        val message = messages[position]
        userProfile.setImageDrawable(getProfileIcon(userProfile.context, message.isByHuman ?: false))
        userMessageText.text = message.content
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isByHuman == true) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
    }

    fun setMessages(chatResponse: ChatResponse) {
        chatResponse.messages?.forEach {
            if (it != null) {
                messages.add(it)
            }
        }
    }

    fun addMessages(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }
}