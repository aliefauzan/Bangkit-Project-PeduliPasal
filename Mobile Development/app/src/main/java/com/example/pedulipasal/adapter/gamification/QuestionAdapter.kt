package com.example.pedulipasal.adapter.gamification

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.data.model.gamification.QuestionsItem
import com.example.pedulipasal.databinding.ItemQuestionBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.removeAsterisk
import com.google.ai.client.generativeai.type.GenerateContentResponse

class QuestionAdapter(
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner,
    private val onItemSelectedCallback: OnItemSelected
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    private val questionList = mutableListOf<QuestionsItem?>()

    interface OnItemSelected {
        fun onOptionClicked(chosenOption: String, correctAnswer: String, currPosition: Int)
        fun onAskGeminiClicked(prompt: String): LiveData<Result<GenerateContentResponse>>
    }

    class ViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questionList[position] ?: QuestionsItem()
        holder.binding.apply {
            setupQuestion(question, position)
            setupGemini(question)
            btnAskGemini.visibility = if (question.answerCorrect) View.GONE else View.VISIBLE
        }
    }

    private fun ItemQuestionBinding.setupQuestion(question: QuestionsItem, position: Int) {
        tvQuestionItem.text = question.question ?: "No Question"
        rgAnswerOptions.removeAllViews()

        question.options?.forEach { option ->
            val radioButton = RadioButton(context).apply {
                text = option
                id = View.generateViewId()
                isChecked = option == question.selectedOption
                isEnabled = !question.alreadyAnswer || !question.answerCorrect
            }

            radioButton.setOnClickListener {
                if (!question.alreadyAnswer || !question.answerCorrect) {
                    handleOptionSelected(question, radioButton.text.toString(), position)
                }
            }
            rgAnswerOptions.addView(radioButton)
        }
    }

    private fun ItemQuestionBinding.handleOptionSelected(
        question: QuestionsItem,
        chosenOption: String,
        position: Int
    ) {
        question.apply {
            alreadyAnswer = true
            selectedOption = chosenOption
            answerCorrect = chosenOption == correctAnswer // Persist correct answer state
        }

        onItemSelectedCallback.onOptionClicked(
            chosenOption,
            question.correctAnswer ?: "No Answer",
            position
        )

        if (question.answerCorrect) {
            disableRadioButtons()
        }

        btnAskGemini.visibility = if (question.answerCorrect) View.GONE else View.VISIBLE
    }


    private fun ItemQuestionBinding.disableRadioButtons() {
        for (i in 0 until rgAnswerOptions.childCount) {
            (rgAnswerOptions.getChildAt(i) as? RadioButton)?.isEnabled = false
        }
    }

    private fun ItemQuestionBinding.setupGemini(question: QuestionsItem) {
        progressBar.visibility = if (question.isLoadingGeminiResponse) View.VISIBLE else View.GONE
        tvGeminiAnswer.visibility = if (question.isAskingGemini) View.VISIBLE else View.GONE
        btnAskGemini.visibility = if (question.isAskingGemini) View.GONE else View.VISIBLE

        btnAskGemini.setOnClickListener {
            btnAskGemini.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            question.isLoadingGeminiResponse = true
            val prompt = buildPrompt(question)
            observeGeminiResponse(prompt, question)
        }
    }

    private fun ItemQuestionBinding.observeGeminiResponse(
        prompt: String,
        question: QuestionsItem
    ) {
        val response = onItemSelectedCallback.onAskGeminiClicked(prompt)
        response.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    tvGeminiAnswer.visibility = View.VISIBLE
                    tvGeminiAnswer.text = removeAsterisk(result.data.text ?: "No Answer")
                    question.isAskingGemini = true
                    question.isLoadingGeminiResponse = false
                }
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    question.isLoadingGeminiResponse = true
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    tvGeminiAnswer.visibility = View.VISIBLE
                    tvGeminiAnswer.text = result.error
                    question.isAskingGemini = true
                    question.isLoadingGeminiResponse = false
                }
            }
        }
    }

    private fun buildPrompt(question: QuestionsItem): String {
        val promptBuilder = StringBuilder()
        promptBuilder.appendLine(question.question ?: "")
        question.options?.forEach { option ->
            promptBuilder.appendLine(option)
        }
        return promptBuilder.toString()
    }

    fun setQuestions(questions: List<QuestionsItem?>) {
        questionList.clear()
        questionList.addAll(questions)
        notifyDataSetChanged()
    }

    fun clearAllItems() {
        questionList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = questionList.size
}

