package com.example.pedulipasal.data.model.gamification

import com.google.gson.annotations.SerializedName

data class QuizModel(

	@field:SerializedName("level")
	val level: String? = null,

	@field:SerializedName("questions")
	val questions: List<QuestionsItem?>? = null
)

data class QuestionsItem(

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("options")
	val options: List<String?>? = null,

	@field:SerializedName("correct_answer")
	val correctAnswer: String? = null
)
