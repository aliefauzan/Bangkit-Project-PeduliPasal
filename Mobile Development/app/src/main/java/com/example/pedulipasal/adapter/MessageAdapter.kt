package com.example.pedulipasal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.helper.getTimeFormat
class MessageAdapter : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val messageItems = mutableListOf<MessageItem>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        return if (messageItems[position].isHuman) {
            R.layout.item_message_local
        } else {
            R.layout.item_message_another_user
        }
    }

    override fun getItemCount(): Int = messageItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageItems[position]
        holder.userMessageText.text = message.content
        holder.tvTime.text = getTimeFormat(message.timestamp)
        if (message.isError == true) {
            holder.userMessageText.setBackgroundResource(R.drawable.rounded_red_background)
            holder.userMessageText.setTextColor(holder.itemView.context.getColor(R.color.md_theme_onTertiary_highContrast))
        }
    }

    fun setMessages(newMessages: List<MessageItem>) {
        val diffCallback = MessageDiffCallback(messageItems, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messageItems.clear()
        messageItems.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addMessage(messageItem: MessageItem) {
        messageItems.add(messageItem)
        notifyItemInserted(messageItems.size - 1)
    }
}

class MessageDiffCallback(
    private val oldList: List<MessageItem>,
    private val newList: List<MessageItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
