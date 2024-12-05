package com.example.pedulipasal.ui.news

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.ChatHomeAdapter
import com.example.pedulipasal.adapter.NewsAdapter
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.response.NewsItem
import com.example.pedulipasal.databinding.FragmentNewsBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.message.MessageActivity
import com.example.pedulipasal.ui.chat.ChatViewModel
import com.example.pedulipasal.ui.settings.SettingsViewModel
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

    private var userId: String = ""

    companion object {
        private const val CHAT_ID_KEY = "detail_chat_key"
        private const val TITLE_KEY = "detail_title_key"
    }

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var chatHomeAdapter: ChatHomeAdapter


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
        setupView()
        setupAction()
        initializeData()
        showNews()
    }

    private fun setupView() {
        settingsViewModel.getSession().observe(viewLifecycleOwner) { user ->
            //Log.d("ProfileActivity", "${user.token}")
            showGreetings(user.userId)
        }
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun showGreetings(userId: String) {
        settingsViewModel.getUserProfileData(userId).observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        //Log.d("ProfileActivity", "${result.data.name} ${result.data.email}")
                        binding.progressBar.visibility = View.GONE

                        binding.tvGreetings.text = "${getCurrentTimeOfDay()} ${result.data.name}"
                    }
                    is Result.Error -> {
                        //Log.d("ProfileActivity", "${result.error} ${result.error}")
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getCurrentTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> getString(R.string.morning)
            in 12..17 -> getString(R.string.afternoon)
            in 18..21 -> getString(R.string.night)
            else -> getString(R.string.night)
        }
    }

    private fun setupAction() {
        binding.btnTryAgain.setOnClickListener { onResume() }
        binding.btnAddNewChat.setOnClickListener { showDialog() }
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
                        binding.noInternetLayout.visibility = View.GONE
                        val listNews = ArrayList<NewsItem>()
                        result.data.forEach {
                            listNews.add(it)
                        }
                        //Log.d("NewsFragment", listNews.size.toString())
                        newsAdapter.submitList(listNews)
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
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

    private fun initializeData() {
        chatViewModel.getSession().observe(viewLifecycleOwner) {user ->
            getHistoryChat(user.userId)
            this.userId = user.userId
        }
    }

    private fun getHistoryChat(userId: String) {
        chatViewModel.getUserHistoryChat(userId).observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        chatHomeAdapter = ChatHomeAdapter(
                            requireActivity(),
                            result.data.sortedByDescending { it.createdAt },
                            object : ChatHomeAdapter.OnItemSelected {
                                override fun onChatButtonClick(chatId: String, title: String) {
                                    moveToMessageActivity(chatId = chatId, title = title)
                                }

                                override fun onButtonDeleteClick(chatId: String) {
                                    showDeleteDialog(chatId)
                                }
                            })

                        binding.rvChats.apply {
                            layoutManager = LinearLayoutManager(
                                requireActivity(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            adapter = chatHomeAdapter
                        }
                        if (chatHomeAdapter.itemCount == 0) {
                            binding.emptyChatLayout.visibility = View.VISIBLE
                        } else {
                            binding.emptyChatLayout.visibility = View.GONE
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
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

    private fun showDeleteDialog(chatId: String) {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.delete_chat))
            setMessage(R.string.delete_chat_confirmation)
            setPositiveButton(R.string.confirm) { _, _ ->
                chatViewModel.deleteChatById(chatId).observe(viewLifecycleOwner) {result ->
                    if (result != null) {
                        when(result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                chatHomeAdapter.deleteItem(chatId)
                                binding.progressBar.visibility = View.GONE
                            }
                            is Result.Error -> {
                                Toast.makeText(requireActivity(), "Gagal menghapus chat dengan id ${result.error}", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    private fun showDialog() {
        val inflater = LayoutInflater.from(requireActivity())
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.ed_new_topics)

        if (this.userId.isNotEmpty()) {
            val userId = this.userId
            AlertDialog.Builder(requireActivity()).apply {
                setTitle(getString(R.string.add_new_chat))
                setView(dialogLayout)
                setPositiveButton(R.string.create) { _, _ ->
                    val title = editText.text.toString().trim()
                    if (title.isNotEmpty()) {
                        createNewChat(title = title, userId = userId)
                    } else {
                        Toast.makeText(context, getString(R.string.empty_topic_warning), Toast.LENGTH_SHORT).show()
                    }
                }
                setNegativeButton(R.string.cancel, null)
                create()
                show()
            }
        }
    }

    private fun createNewChat(title: String, userId: String) {
        val createChatRequest = CreateChatRequest (
            userId = userId,
            title = title
        )
        chatViewModel.createChat(createChatRequest).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        result.data.chatId.let { moveToMessageActivity(chatId = it, title = title) }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        showNews()
        initializeData()
    }
}