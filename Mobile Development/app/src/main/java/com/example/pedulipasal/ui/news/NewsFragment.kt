package com.example.pedulipasal.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.adapter.NewsAdapter
import com.example.pedulipasal.data.model.NewsItem
import com.example.pedulipasal.databinding.FragmentNewsBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory

class NewsFragment : Fragment() {

    private val newsViewModel by viewModels<NewsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showNews()
    }

    private fun showNews() {
        newsAdapter = NewsAdapter()
        newsViewModel.getNews().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        val listNews = ArrayList<NewsItem>()
                        result.data.forEach {
                            listNews.add(it)
                        }
                        Log.d("NewsFragment", listNews.size.toString())
                        newsAdapter.submitList(listNews)
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }

            binding.rvNews.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = newsAdapter
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}