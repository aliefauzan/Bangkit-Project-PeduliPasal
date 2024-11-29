package com.example.pedulipasal.ui.quiz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.FragmentNewsBinding
import com.example.pedulipasal.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

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
        setupSpinner()
    }


    private fun setupSpinner() {
        //val spinner: Spinner = binding.spinner
        val levels = resources.getStringArray(R.array.levels)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, levels)
        binding.autoCompleteLevelMenu.setAdapter(arrayAdapter)

        binding.autoCompleteLevelMenu.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedLevel = levels[position]
                Toast.makeText(requireContext(), selectedLevel, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        setupSpinner()
    }

}