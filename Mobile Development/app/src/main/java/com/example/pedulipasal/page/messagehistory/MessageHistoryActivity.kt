package com.example.pedulipasal.page.messagehistory

import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.MessageAdapter
import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.databinding.ActivityLoginBinding
import com.example.pedulipasal.databinding.ActivityMessageHistoryBinding

class MessageHistoryActivity : AppCompatActivity() {

    private var chatResponse: ChatResponse? = null
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var binding: ActivityMessageHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        chatResponse = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("detail_chat_key", ChatResponse::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("detail_chat_key")
        }

        val title = chatResponse?.title ?: "No title"
        if (chatResponse != null) {
            supportActionBar?.title = title
        }
    }

    private fun setupAction() {
        messageAdapter = MessageAdapter()
        messageAdapter.setMessages(chatResponse!!)
        binding.rvMessageHistory.apply {
            layoutManager = LinearLayoutManager(this@MessageHistoryActivity)
            adapter = messageAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history_message_page, menu)
        return super.onCreateOptionsMenu(menu)
    }
}