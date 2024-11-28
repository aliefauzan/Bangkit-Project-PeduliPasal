package com.example.pedulipasal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R

class MessageSuggestionAdapter(
    private val suggestions: List<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<MessageSuggestionAdapter.SuggestionViewHolder>() {

    inner class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val suggestionText: TextView = view.findViewById(R.id.tv_suggestion_text)

        fun bind(suggestion: String) {
            suggestionText.text = suggestion
            itemView.setOnClickListener {
                onItemClicked(suggestion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size
}
