package com.example.pedulipasal.adapter.gamification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.data.model.gamification.QuestionsItem
import com.example.pedulipasal.databinding.ItemQuestionBinding

class QuestionAdapter(
    private val context: Context,
    private val questionList: List<QuestionsItem?>?, // Made non-nullable
    private val onItemSelectedCallback: OnItemSelected
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    interface OnItemSelected {
        fun onOptionClicked(chosenOption: String, correctAnswer: String)
    }

    class ViewHolder(val binding: ItemQuestionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questionList?.get(position) ?: QuestionsItem()
        holder.binding.apply {
            tvQuestionItem.text = question.question ?: "No Question"
            rgAnswerOptions.removeAllViews()

            question.options?.forEachIndexed { _, option ->
                val radioButton = RadioButton(context).apply {
                    text = option
                    id = View.generateViewId()
                }
                radioButton.setOnClickListener {
                    onItemSelectedCallback.onOptionClicked(
                        chosenOption = radioButton.text.toString(),
                        correctAnswer = question.correctAnswer ?: "No Answer"
                    )
                }
                // Add radio button to RadioGroup
                rgAnswerOptions.addView(radioButton)
            }
        }
    }

    override fun getItemCount(): Int {
        return questionList?.size ?: -1
    }
}
