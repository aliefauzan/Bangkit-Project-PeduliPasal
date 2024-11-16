package com.example.pedulipasal.page.chat

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.MessageAdapter
import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.data.model.Message
import com.example.pedulipasal.databinding.ActivityChatBinding
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private var chatResponse: ChatResponse? = null
    private val messages: MutableList<Message> = mutableListOf()

    companion object {
        const val DETAIL_CHAT_KEY = "detail_chat_key"
        const val TOPIC_KEY = "topic_key"
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
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(DETAIL_CHAT_KEY)) {
            // Existing chat
            chatResponse = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(DETAIL_CHAT_KEY, ChatResponse::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(DETAIL_CHAT_KEY)
            }

            val title = chatResponse?.title ?: "No title"
            supportActionBar?.title = title

            // Initialize messages with existing chat messages
            chatResponse?.messages?.let { messages.addAll(it) }
            binding.messageInputLayout.visibility = View.GONE
        } else if (intent.hasExtra(TOPIC_KEY)) {
            // New chat
            val topic = intent.getStringExtra(TOPIC_KEY) ?: "New Chat"
            supportActionBar?.title = topic

            // Initialize a new ChatResponse
            chatResponse = ChatResponse(
                chatId = "chat_new",
                userId = "user_id", // Replace with actual user ID
                title = topic,
                createdAt = Date(),
                updateAt = Date(),
                messages = messages
            )
        } else {
            // No data provided
            Toast.makeText(this, "No chat data provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize RecyclerView and Adapter
        messageAdapter = MessageAdapter()
        messageAdapter.setMessages(messages)
        binding.rvMessageHistory.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }
    }

    private fun setupAction() {
        // Send button click listener
        binding.btnSendMessage.setOnClickListener {
            sendMessage()
        }

        // Handle 'Done' action in the keyboard
        binding.etMessageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun sendMessage() {
        val content = binding.etMessageInput.text.toString().trim()
        if (content.isNotEmpty()) {
            val newMessage = Message(
                messageId = "msg${messages.size + 1}",
                isByHuman = true,
                content = content,
                timestamp = Date()
            )
            messages.add(newMessage)
            messageAdapter.addMessage(newMessage)
            binding.rvMessageHistory.scrollToPosition(messages.size - 1)
            binding.etMessageInput.text?.clear()

            // TODO: Handle sending the message to the bot or API
        } else {
            Toast.makeText(this, getString(R.string.empty_message_warning), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
