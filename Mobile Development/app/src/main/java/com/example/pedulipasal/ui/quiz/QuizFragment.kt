package com.example.pedulipasal.ui.quiz

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.gamification.QuestionAdapter
import com.example.pedulipasal.data.model.gamification.QuestionsItem
import com.example.pedulipasal.data.model.gamification.QuizModel
import com.example.pedulipasal.databinding.FragmentQuizBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private lateinit var questionAdapter: QuestionAdapter
    private var currScore: Int = 0

    private val quizViewModel by viewModels<QuizViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropDownMenu()
        setupAction()
    }

    private fun setupDropDownMenu() {
        val levels = resources.getStringArray(R.array.levels)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, levels)
        binding.autoCompleteLevelMenu.setAdapter(arrayAdapter)

        binding.autoCompleteLevelMenu.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedLevel = levels[position]
                setupQuiz(selectedLevel)
            }
    }

    private fun setupQuiz(selectedLevel: String) {
        val jsonFileName = when (selectedLevel) {
            "Easy" -> "Beginner.json"
            "Intermediate" -> "Intermediate.json"
            "Advanced" -> "Advanced.json"
            else -> null
        }

        if (jsonFileName != null) {
            val questions = loadQuestionsFromJson(jsonFileName)
            if (questions != null) {
                Log.d("QuizFragment", "Questions: ${questions.size}")
                questionAdapter = QuestionAdapter(requireActivity(), viewLifecycleOwner, object : QuestionAdapter.OnItemSelected {
                    override fun onOptionClicked(chosenOption: String, correctAnswer: String, currPostion: Int) {
                        if (chosenOption == correctAnswer) {
                            currScore += 10
                            binding.tvScoreValue.text = currScore.toString()
                            scrollToNextQuestion(nextPosition = currPostion + 1)
                        }
                    }

                    override fun onAskGeminiClicked(prompt: String): LiveData<Result<GenerateContentResponse>> {
                        return quizViewModel.getGeminiResponse(prompt)
                    }
                })
                questionAdapter.setQuestions(questions)

                binding.rvQuizQuestion.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = questionAdapter
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load quiz questions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scrollToNextQuestion(nextPosition: Int) {
        if (nextPosition < questionAdapter.itemCount) {
            binding.rvQuizQuestion.post {
                binding.rvQuizQuestion.smoothScrollToPosition(nextPosition)
            }
        }
    }

    private fun setupAction() {
        binding.btnReset.setOnClickListener {
            scrollToTop()
            binding.tvScoreValue.text = "0"
            currScore = 0
            if (binding.rvQuizQuestion.adapter != null) {
                questionAdapter.clearAllItems()
            }
        }
    }

    private fun scrollToTop() {
        binding.rvQuizQuestion.post {
            binding.rvQuizQuestion.smoothScrollToPosition(0)
        }
    }

    private fun loadQuestionsFromJson(fileName: String): List<QuestionsItem?>? {
        try {
            val assetManager = requireContext().assets
            val inputStream = assetManager.open(fileName)
            val reader = InputStreamReader(inputStream)
            val quizDataType = object : TypeToken<QuizModel>() {}.type
            val quizData: QuizModel = Gson().fromJson(reader, quizDataType)
            return quizData.questions
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("QuizFragment", "Error loading questions: ${e.message}")
            return null
        }
    }

    override fun onResume() {
        super.onResume()
        setupDropDownMenu()
    }

}