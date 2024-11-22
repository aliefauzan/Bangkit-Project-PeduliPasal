package com.example.pedulipasal.page.message

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
import androidx.recyclerview.widget.RecyclerView
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.MessageAdapter
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
    private val messageItems: MutableList<MessageItem> = mutableListOf()

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

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()

        val chatId = intent.getStringExtra(CHAT_ID_KEY)
        if (intent.hasExtra(CHAT_ID_KEY)) {
            //Log.d("MessageActivity", "Received chatId: $chatId")
            if (!chatId.isNullOrEmpty()) {
                showListMessages(chatId) // Display messages for the chatId
            }
        }
    }

    private fun setupView() {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize the adapter
        messageAdapter = MessageAdapter()

        //Log.d("MessageActivity", "${result.data.chatId}")

        // Set up the RecyclerView with LayoutManager and Adapter
        binding.rvMessageHistory.apply {
            layoutManager = LinearLayoutManager(this@MessageActivity)
            adapter = messageAdapter
        }
    }

    private fun showListMessages(chatId: String) {
        messageViewModel.getChatMessageById(chatId).removeObservers(this)
        messageViewModel.getChatMessageById(chatId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    supportActionBar?.title = result.data.title
                    // Update the adapter's data
                    result.data.messages?.let {
                        messageAdapter.setMessages(it)
                    }
                    // Scroll to the last message
                    if (messageAdapter.itemCount != 0) {
                        binding.rvMessageHistory.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                    result.data.chatId?.let {
                        setupAction(it)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    //Log.d("MessageActivity", "${result.error}")
                }
            }
        }
    }

    private fun setupAction(chatId: String) {
        // Send button click listener
        //Log.d("chatActivity", "setup action jalan")

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

        binding.rvMessageHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val itemCount = recyclerView.adapter?.itemCount ?: 0

                if (lastVisibleItemPosition < itemCount - 1) {
                    binding.btnJumpToNewest.visibility = View.VISIBLE
                } else {
                    binding.btnJumpToNewest.visibility = View.GONE
                }
            }
        })

        binding.btnJumpToNewest.setOnClickListener {
            binding.rvMessageHistory.layoutManager as LinearLayoutManager
            binding.rvMessageHistory.smoothScrollToPosition(
                binding.rvMessageHistory.adapter?.itemCount ?: (0 - 1)
            )
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
        //Log.d("MessageActivity", "value from process: ${chatId} ${addMessageRequest.isHuman} ${addMessageRequest.content}")

        // Add the new message to the adapter
        messageAdapter.addMessage(
            MessageItem(
                isHuman = addMessageRequest.isHuman,
                content = addMessageRequest.content,
                timestamp = showLocalTime(Date())
            )
        )

        // Scroll to the last message
        binding.rvMessageHistory.smoothScrollToPosition(messageAdapter.itemCount - 1)

        messageViewModel.addMessageToChat(chatId, addMessageRequest).removeObservers(this)
        messageViewModel.addMessageToChat(chatId, addMessageRequest).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    messageAdapter.addMessage(
                        MessageItem(
                            isHuman = false,
                            content = result.data.aiMessage?.content ?: "Error message",
                            timestamp = showLocalTime(Date())
                        )
                    )
                    binding.rvMessageHistory.post {
                        binding.rvMessageHistory.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
