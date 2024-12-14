package com.example.pedulipasal.page.message

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import com.example.pedulipasal.helper.filteredString
import com.example.pedulipasal.helper.getDateFormat
import com.example.pedulipasal.helper.getDayOfWeek
import com.example.pedulipasal.helper.getTimeFormat
import com.example.pedulipasal.helper.removeNewLine
import com.example.pedulipasal.helper.showLocalTime
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageSuggestionAdapter: MessageSuggestionAdapter
    private lateinit var listSuggestion: List<String>
    private var lastClickTime = 0L
    private lateinit var chatId: String
    private lateinit var title: String
    private lateinit var messages: MutableList<MessageItem>

    private val messageViewModel by viewModels<MessageViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val CHAT_ID_KEY = "detail_chat_key"
        const val TITLE_KEY = "detail_title_key"
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

        this.chatId = intent.getStringExtra(CHAT_ID_KEY) ?: ""
        this.title = intent.getStringExtra(TITLE_KEY) ?: ""
        if (chatId.isNotEmpty() && title.isNotEmpty()) {
            showListMessages(chatId = chatId, title = title)
        }
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listSuggestion = listOf(
            "Apa pasal yang sesuai dengan kasus pencurian motor",
            "Sengketa tanah termasuk dalam pasal apa",
            "Jelaskan pasal yang mengatur tentang kasus harta warisan"
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

    private fun showListMessages(chatId: String, title: String) {
        messageViewModel.getChatMessageById(chatId).observe(this) { result ->
            when (result) {
                is Result.Loading -> toggleProgressBarVisibility(true)
                is Result.Success -> {
                    supportActionBar?.title = title
                    result.data.let {
                        messageAdapter.setMessages(it)
                    }
                    scrollToLastMessage()
                    setupAction(chatId)
                    toggleProgressBarVisibility(false)
                }
                is Result.Error -> {
                    toggleProgressBarVisibility(false)
                    Toast.makeText(this, getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction(chatId: String) {
        binding.btnSendMessage.setOnClickListener {
            sendMessage(chatId)
        }

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

        binding.etMessageInput.text?.clear()

        messageAdapter.addMessage(
            MessageItem(
                messageId = "",
                isHuman = addMessageRequest.isHuman,
                content = addMessageRequest.content,
                timestamp = showLocalTime(Date()),
                chatId = chatId
            )
        )

        scrollToLastMessage()

        messageViewModel.addMessageToChat(chatId, addMessageRequest).observe(this) { result ->
            when (result) {
                is Result.Loading -> toggleProgressBarVisibility(true)
                is Result.Success -> {
                    toggleProgressBarVisibility(false)
                    messageAdapter.addMessage(
                        MessageItem(
                            messageId = "",
                            isHuman = false,
                            content = filteredString(result.data.aiMessage?.content ?: "Error message"),
                            timestamp = showLocalTime(Date()),
                            chatId = chatId,
                        )
                    )
                    scrollToLastMessage()
                }
                is Result.Error -> {
                    toggleProgressBarVisibility(false)
                    Toast.makeText(this, getString(R.string.offline_message), Toast.LENGTH_SHORT).show()
                    scrollToLastMessage()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat_activity_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                if (this.title.isNotEmpty()) {
                    writeToFile(this, title, messageAdapter.messageItems)
                    shareChats(this, title)
                    return true
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun writeToFile(context: Context, fileName: String, listMessage: List<MessageItem>) {
        val textFileFilename = "$fileName.txt"
        val file = File(context.filesDir, textFileFilename)
        try {
            var prevDate = ""
            val fos = FileOutputStream(file)
            val textHeader = "[PeduliPasal] Chat history in title $fileName \n"
            fos.write(textHeader.toByteArray())
            val savedOn = "Saved on: ${getDateFormat(Date())} ${getTimeFormat(Date())} \n"
            fos.write(savedOn.toByteArray())
            listMessage.forEach {
                if (getDateFormat(it.timestamp) != prevDate) {
                    val date = "\n${getDayOfWeek(it.timestamp)}, ${getDateFormat(it.timestamp)}\n"
                    fos.write(date.toByteArray())
                    prevDate = getDateFormat(it.timestamp)
                }
                val username = if (it.isHuman) "Human" else "AI"
                val message = "${getTimeFormat(it.timestamp)}\t${username}\t${removeNewLine(it.content)} \n"
                fos.write(message.toByteArray())
            }
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareChats(context: Context, fileName: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val uri = getFileUri(this, "$fileName.txt")
        if (uri != null) {
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(Intent.createChooser(shareIntent, "Share File"))
        }
    }

    private fun getFileUri(context: Context, fileName: String): Uri? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } else {
            null
        }
    }
}
