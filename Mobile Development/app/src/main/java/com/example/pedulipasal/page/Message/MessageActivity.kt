package com.example.pedulipasal.page.Message

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
import com.example.pedulipasal.data.model.response.ChatItem
import com.example.pedulipasal.data.model.response.MessageItem
import com.example.pedulipasal.databinding.ActivityMessageBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private var chatItem: ChatItem? = null
    private val messageItems: MutableList<MessageItem> = mutableListOf()


    private val messageViewModel by viewModels<MessageViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val CHAT_ID_KEY = "detail_chat_key"
        const val TOPIC_KEY = "topic_key"
        const val USER_ID_KEY = "user_id_key"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        val chatId = intent.getStringExtra(CHAT_ID_KEY)
        if (intent.hasExtra(CHAT_ID_KEY)) {
            Log.d("MessageActivity", "Received chatId: $chatId")
            if (!chatId.isNullOrEmpty()) {
                showListMessages(chatId) // Display messages for the chatId
            }
        }
    }

    private fun setupView() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun showListMessages(chatId: String) {
        messageViewModel.getChatMessageById(chatId).removeObservers(this)
        messageViewModel.getChatMessageById(chatId).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        this.chatItem = result.data
                        supportActionBar?.title = result.data.chatId

                        // Initialize RecyclerView and Adapter
                        messageAdapter = MessageAdapter()
                        result.data.messageItems.let {
                            if (it != null) {
                                messageAdapter.setMessages(it)
                            }
                        }
                        binding.rvMessageHistory.apply {
                            layoutManager = LinearLayoutManager(this@MessageActivity)
                            adapter = messageAdapter
                        }
                        result.data.chatId?.let { setupAction(it) }
                        Toast.makeText(this, "${result.data.title}", Toast.LENGTH_SHORT).show()
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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
            val newMessageItem = MessageItem(
                messageId = "0001",
                isByHuman = true,
                content = content,
                timestamp = Date()
            )
            messageAdapter.addMessage(newMessageItem)
            binding.rvMessageHistory.apply {
                layoutManager = LinearLayoutManager(this@MessageActivity)
                adapter = messageAdapter
            }
            binding.rvMessageHistory.scrollToPosition(messageItems.size - 1)
            binding.etMessageInput.text?.clear()
            messageViewModel.addMessageToChat(chatId,newMessageItem).removeObservers(this)

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
