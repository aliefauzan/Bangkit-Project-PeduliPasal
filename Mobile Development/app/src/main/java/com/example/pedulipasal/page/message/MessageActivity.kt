package com.example.pedulipasal.page.message

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.MessageAdapter
import com.example.pedulipasal.adapter.MessageSuggestionAdapter
import com.example.pedulipasal.data.model.request.AddMessageRequest
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.databinding.ActivityMessageBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.helper.showLocalTime
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageSuggestionAdapter: MessageSuggestionAdapter
    private lateinit var listSuggestion: List<String>
    private var lastClickTime = 0L

    private val messageViewModel by viewModels<MessageViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val CHAT_ID_KEY = "detail_chat_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()

        val chatId = intent.getStringExtra(CHAT_ID_KEY)
        if (!chatId.isNullOrEmpty()) {
            showListMessages(chatId)
        }
    }

    private fun setupView() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listSuggestion = listOf(
            "Sebutkan 5 sila Pancasila",
            "Jelaskan secara singkat pembentukan UUD 1945",
            "Sebutkan suku yang terkenal di indonesia"
        )

        messageAdapter = MessageAdapter()
        messageSuggestionAdapter = MessageSuggestionAdapter(listSuggestion) { message ->
            if (System.currentTimeMillis() - lastClickTime > 300) {
                lastClickTime = System.currentTimeMillis()
                fillTextInput(message)
            }
        }

        binding.rvMessageHistory.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            adapter = messageAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    toggleScrollToLastButton()
                }
            })
        }

        binding.rvMessageSuggestion.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = messageSuggestionAdapter
        }

        binding.btnJumpToNewest.setOnClickListener {
            scrollToLastMessage()
        }
    }

    private fun showListMessages(chatId: String) {
        messageViewModel.getChatMessageById(chatId).observe(this) { result ->
            when (result) {
                is Result.Loading -> toggleProgressBarVisibility(true)
                is Result.Success -> {
                    toggleProgressBarVisibility(false)
                    supportActionBar?.title = result.data.title
                    result.data.messages?.let {
                        messageAdapter.setMessages(it)
                    }
                    scrollToLastMessage()
                    setupAction(chatId)
                }
                is Result.Error -> {
                    toggleProgressBarVisibility(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction(chatId: String) {
        // Send button click listener
        binding.btnSendMessage.setOnClickListener {
            sendMessage(chatId)
        }

        // Handle 'Done' action in the keyboard
        binding.etMessageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage(chatId)
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage(chatId: String) {
        val content = binding.etMessageInput.text.toString().trim()
        if (content.isNotEmpty()) {
            sendMessageFromHuman(chatId, content)
        } else {
            Toast.makeText(this, getString(R.string.empty_message_warning), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessageFromHuman(chatId: String, content: String) {
        val addMessageRequest = AddMessageRequest(
            isHuman = true,
            content = content
        )

        // Clear the input field
        binding.etMessageInput.text?.clear()

        // Add the new message to the adapter
        messageAdapter.addMessage(
            MessageItem(
                isHuman = addMessageRequest.isHuman,
                content = addMessageRequest.content,
                timestamp = showLocalTime(Date())
            )
        )

        // Scroll to the last message
        scrollToLastMessage()

        messageViewModel.addMessageToChat(chatId, addMessageRequest).observe(this) { result ->
            when (result) {
                is Result.Loading -> toggleProgressBarVisibility(true)
                is Result.Success -> {
                    toggleProgressBarVisibility(false)
                    messageAdapter.addMessage(
                        MessageItem(
                            isHuman = false,
                            content = result.data.aiMessage?.content ?: "Error message",
                            timestamp = showLocalTime(Date())
                        )
                    )
                    scrollToLastMessage()
                }
                is Result.Error -> {
                    toggleProgressBarVisibility(false)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun scrollToLastMessage() {
        if (messageAdapter.itemCount > 0) {
            binding.rvMessageHistory.post {
                binding.rvMessageHistory.smoothScrollToPosition(messageAdapter.itemCount - 1)
            }
        }
    }

    private fun toggleScrollToLastButton() {
        val layoutManager = binding.rvMessageHistory.layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val isAtBottom = lastVisibleItemPosition == messageAdapter.itemCount - 1
        binding.btnJumpToNewest.visibility = if (isAtBottom) View.GONE else View.VISIBLE
    }

    private fun toggleProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun fillTextInput(prompt: String) {
        binding.etMessageInput.setText(prompt)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
