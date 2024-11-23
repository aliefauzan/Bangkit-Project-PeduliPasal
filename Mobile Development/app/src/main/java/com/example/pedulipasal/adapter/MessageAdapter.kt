package com.example.pedulipasal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.helper.getProfileIcon
import com.example.pedulipasal.helper.getTimeFormat


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val messageItems = ArrayList<MessageItem>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfile: ImageView = view.findViewById(R.id.iv_userProfile)
        val userMessageText: TextView = view.findViewById(R.id.tv_userMessageText)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
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
        val isByHuman = messageItems[position].isHuman
        //Log.d("MessageAdapter", "getItemViewType - position: $position, isByHuman: $isByHuman")
        return if (isByHuman) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
    }

    override fun getItemCount(): Int = messageItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageItems[position]
        holder.userProfile.setImageDrawable(getProfileIcon(holder.userProfile.context, message.isHuman ?: false))
        holder.userMessageText.text = message.content
        holder.tvTime.text = getTimeFormat(messageItems[position].timestamp)
    }

    fun setMessages(messagesList: List<MessageItem>) {
        messageItems.clear()
        messageItems.addAll(messagesList)
        notifyDataSetChanged()
    }

    fun addMessage(messageItem: MessageItem) {
        messageItems.add(messageItem)
        notifyItemInserted(messageItems.size - 1)
    }
}