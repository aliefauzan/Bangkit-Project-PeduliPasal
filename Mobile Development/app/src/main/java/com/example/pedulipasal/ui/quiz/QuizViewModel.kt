package com.example.pedulipasal.ui.quiz

import androidx.lifecycle.ViewModel
import com.example.pedulipasal.data.CloudRepository

class QuizViewModel(
    private val cloudRepository: CloudRepository
): ViewModel() {
    fun getGeminiResponse(prompt: String) = cloudRepository.getGeminiAiResponse(prompt)
}