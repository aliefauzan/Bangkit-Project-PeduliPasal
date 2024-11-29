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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.gamification.QuestionAdapter
import com.example.pedulipasal.data.model.gamification.QuestionsItem
import com.example.pedulipasal.data.model.gamification.QuizModel
import com.example.pedulipasal.databinding.FragmentQuizBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private lateinit var questionAdapter: QuestionAdapter
    private var currScore: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setupDropDownMenu()
    }


    private fun setupDropDownMenu() {
        //val spinner: Spinner = binding.spinner
        val levels = resources.getStringArray(R.array.levels)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, levels)
        binding?.autoCompleteLevelMenu?.setAdapter(arrayAdapter)

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
                questionAdapter = QuestionAdapter(requireActivity(), questions, object : QuestionAdapter.OnItemSelected {
                    override fun onOptionClicked(chosenOption: String, correctAnswer: String) {
                        if (chosenOption == correctAnswer) {
                            currScore += 10
                            binding.tvScoreValue.text = currScore.toString()
                        }
                        Toast.makeText(requireContext(), "Chosen Option: $chosenOption, Correct Answer: $correctAnswer", Toast.LENGTH_SHORT).show()
                    }
                })

                binding.rvQuizQuestion.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = questionAdapter
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load quiz questions", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadQuestionsFromJson(fileName: String): List<QuestionsItem?>? {
        try {
            // Open the JSON file from assets
            val assetManager = requireContext().assets
            val inputStream = assetManager.open(fileName)
            val reader = InputStreamReader(inputStream)

            // Use the correct TypeToken for QuizData, which contains questions
            val quizDataType = object : TypeToken<QuizModel>() {}.type
            val quizData: QuizModel = Gson().fromJson(reader, quizDataType)

            // Return the list of questions
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