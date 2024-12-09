package com.example.pedulipasal.ui.news

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.ChatHomeAdapter
import com.example.pedulipasal.adapter.NewsAdapter
import com.example.pedulipasal.adapter.NewsCategoryAdapter
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.response.NewsItem
import com.example.pedulipasal.databinding.FragmentNewsBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.message.MessageActivity
import com.example.pedulipasal.ui.chat.ChatViewModel
import com.example.pedulipasal.ui.settings.SettingsViewModel
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialFadeThrough
import java.util.Calendar

class NewsFragment : Fragment() {

    private val newsViewModel by viewModels<NewsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val chatViewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var chatHomeAdapter: ChatHomeAdapter
    private lateinit var newsCategoryAdapter: NewsCategoryAdapter

    private var userId: String = ""

    companion object {
        private const val CHAT_ID_KEY = "detail_chat_key"
        private const val TITLE_KEY = "detail_title_key"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 600L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(ContextCompat.getColor(requireContext(), R.color.md_theme_background))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        ViewCompat.setTransitionName(binding.topContainerView, getString(R.string.login_button_transition_name))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeObserver()
        setupUserSessionObserver()
        setupCategoryList()
        setupActions()
        setupNewsObserver(null)
        setupChatObserver()
    }

    private fun setupThemeObserver() {
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setupUserSessionObserver() {
        settingsViewModel.getSession().observe(viewLifecycleOwner) { user ->
            userId = user.userId
            showGreetings(user.userId)
        }
    }

    private fun setupCategoryList() {
        val categories = listOf("general", "entertainment", "business", "health", "science", "sports", "technology")
        newsCategoryAdapter = NewsCategoryAdapter(
            categories,
            onCategorySelected = { category -> setupNewsObserver(category) },
            defaultCategory = categories[0]
        )

        binding.rvNewsCategory.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newsCategoryAdapter
        }
    }

    private fun setupActions() {
        binding.btnTryAgain.setOnClickListener {
            setupNewsObserver(null)
        }
        binding.btnAddNewChat.setOnClickListener { showCreateChatDialog() }
    }

    private fun setupNewsObserver(category: String?) {
        newsAdapter = NewsAdapter()
        newsViewModel.getNews(category).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.newsProgressBar.visibility = View.VISIBLE
                        binding.noInternetLayout.visibility = View.GONE
                    }
                    is Result.Success -> {
                        binding.noInternetLayout.visibility = View.GONE

                        val filteredNews = result.data.filter { newsItem ->
                            !(newsItem.title?.contains("[Removed]", ignoreCase = true) == true)
                        }

                        newsAdapter.submitList(filteredNews)
                        binding.newsProgressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.newsProgressBar.visibility = View.GONE
                        binding.noInternetLayout.visibility = View.VISIBLE
                    }
                }
            }

            binding.rvNews.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = newsAdapter
            }
        }
    }

    private fun showGreetings(userId: String) {
        settingsViewModel.getUserProfileData(userId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.isVisible = true
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    binding.tvGreetings.text = "${getCurrentTimeOfDay()} ${result.data.name}"
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireActivity(), getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentTimeOfDay(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> getString(R.string.morning)
            in 12..17 -> getString(R.string.afternoon)
            else -> getString(R.string.night)
        }
    }

    private fun setupChatObserver() {
        chatViewModel.getSession().observe(viewLifecycleOwner) { user ->
            getHistoryChat(user.userId)
        }
    }

    private fun getHistoryChat(userId: String) {
        chatViewModel.getUserHistoryChat(userId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.isVisible = true
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    val chats = result.data.sortedByDescending { it.createdAt }
                    chatHomeAdapter = ChatHomeAdapter(requireActivity(), chats, object : ChatHomeAdapter.OnItemSelected {
                        override fun onChatButtonClick(chatId: String, title: String) {
                            moveToMessageActivity(chatId, title)
                        }

                        override fun onButtonDeleteClick(chatId: String) {
                            showDeleteChatDialog(chatId)
                        }
                    })

                    binding.rvChats.apply {
                        layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
                        adapter = chatHomeAdapter
                    }
                    binding.emptyChatLayout.isVisible = chatHomeAdapter.itemCount == 0
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireActivity(), getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun moveToMessageActivity(chatId: String, title: String) {
        val intent = Intent(requireActivity(), MessageActivity::class.java).apply {
            putExtra(CHAT_ID_KEY, chatId)
            putExtra(TITLE_KEY, title)
        }
        startActivity(intent)
    }

    private fun showDeleteChatDialog(chatId: String) {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.delete_chat))
            setMessage(R.string.delete_chat_confirmation)
            setPositiveButton(R.string.confirm) { _, _ -> deleteChat(chatId) }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun deleteChat(chatId: String) {
        chatViewModel.deleteChatById(chatId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.isVisible = true
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    chatHomeAdapter.deleteItem(chatId)
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireActivity(), getString(R.string.delete_chat_failed, result.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showCreateChatDialog() {
        if (userId.isEmpty()) return

        val inflater = LayoutInflater.from(requireActivity())
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.ed_new_topics)

        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.add_new_chat))
            setView(dialogLayout)
            setPositiveButton(R.string.create) { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    createNewChat(title, userId)
                } else {
                    Toast.makeText(context, getString(R.string.empty_topic_warning), Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    private fun createNewChat(title: String, userId: String) {
        val createChatRequest = CreateChatRequest(userId = userId, title = title)
        chatViewModel.createChat(createChatRequest).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.isVisible = true
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    result.data.chatId.let { moveToMessageActivity(it, title) }
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireActivity(), getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
