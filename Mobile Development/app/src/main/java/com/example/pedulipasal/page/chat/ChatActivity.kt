package com.example.pedulipasal.page.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.MessageAdapter
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.data.model.response.ChatResponse
import com.example.pedulipasal.data.model.response.Message
import com.example.pedulipasal.databinding.ActivityChatBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private var chatResponse: ChatResponse? = null
    private val messages: MutableList<Message> = mutableListOf()
    private var isChatCreated = false


    private val chatViewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(this@ChatActivity)
    }

    companion object {
        const val CHAT_ID_KEY = "detail_chat_key"
        const val TOPIC_KEY = "topic_key"
        const val USER_ID_KEY = "user_id_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
    }

    private fun setupView() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(CHAT_ID_KEY)) {
            // Existing chat
            val chatID = if (Build.VERSION.SDK_INT >= 33) {
                intent.getStringExtra(CHAT_ID_KEY)
            } else {
                @Suppress("DEPRECATION")
                intent.getStringExtra(CHAT_ID_KEY)
            }
            if (chatID != null) {
                showMessageHistory(chatID)
            }

        } else if (intent.hasExtra(TOPIC_KEY) && intent.hasExtra(USER_ID_KEY) && !isChatCreated) {
            // New chat
            isChatCreated = true
            val userId = intent.getStringExtra(USER_ID_KEY) ?: "-1"
            val title = intent.getStringExtra(TOPIC_KEY) ?: "-1"
            supportActionBar?.title = title
            val createChatRequest = CreateChatRequest (
                userId = userId,
                title = title
            )
            chatViewModel.createChat(createChatRequest).observe(this) { result ->
                if (result != null) {
                    when(result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val chatId = result.data.chatId ?: "-1"
                            chatWithBot(chatId)
                            Log.d("ChatActivity", "Berhasil membuat chat baru dengan id ${result.data.chatId}")
                            Toast.makeText(this, "Berhasil membuat chat baru dengan id ${result.data.chatId}", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Log.d("ChatActivity", "Gagal membuat chat baru dengan id ${result.error}")
                            Toast.makeText(this, "Gagal membuat chat baru dengan id ${result.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            // No data provided
            Toast.makeText(this, "No chat data provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun chatWithBot(chatId: String) {
        messageAdapter = MessageAdapter()
        setupAction(chatId)
    }

    private fun setupAction(chatId: String) {
        // Send button click listener
        Log.d("chatActivity", "setup action jalan")
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
            val newMessage = Message(
                messageId = "0001",
                isByHuman = true,
                content = content,
                timestamp = Date()
            )
            messageAdapter.addMessage(newMessage)
            binding.rvMessageHistory.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = messageAdapter
            }
            binding.rvMessageHistory.scrollToPosition(messages.size - 1)
            binding.etMessageInput.text?.clear()
            chatViewModel.addMessageToChat(chatId,newMessage).removeObservers(this)

            // TODO: Handle sending the message to the bot or API
        } else {
            Toast.makeText(this, getString(R.string.empty_message_warning), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showMessageHistory(chatId: String) {
        chatViewModel.getChatMessageById(chatId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        this.chatResponse = result.data
                        supportActionBar?.title = result.data.title

                        // Initialize RecyclerView and Adapter
                        messageAdapter = MessageAdapter()
                        result.data.messages?.let { messageAdapter.setMessages(it) }
                        binding.rvMessageHistory.apply {
                            layoutManager = LinearLayoutManager(this@ChatActivity)
                            adapter = messageAdapter
                        }
                        binding.messageInputLayout.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isChatCreated", isChatCreated)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isChatCreated = savedInstanceState.getBoolean("isChatCreated", false)
    }
}
