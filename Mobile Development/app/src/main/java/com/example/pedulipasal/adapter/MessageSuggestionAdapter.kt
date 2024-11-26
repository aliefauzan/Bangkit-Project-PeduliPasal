package com.example.pedulipasal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.databinding.ItemMessageSuggestionBinding

class MessageSuggestionAdapter(
    private val listSuggestion: List<String>,
    private val onItemSelectedCallback: OnItemSelected
): RecyclerView.Adapter<MessageSuggestionAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onItemClicked(message: String)
    }

    class ViewHolder(val binding: ItemMessageSuggestionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageSuggestionBinding.inflate (
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSuggestion.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvSuggestionText.text = listSuggestion[position]
        }

        holder.binding.tvSuggestionText.setOnClickListener {
            listSuggestion[position].let { it1 ->
                onItemSelectedCallback.onItemClicked(it1)
            }
        }
    }
}