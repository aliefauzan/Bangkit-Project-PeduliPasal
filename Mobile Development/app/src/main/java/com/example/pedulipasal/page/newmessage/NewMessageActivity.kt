package com.example.pedulipasal.page.newmessage

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.ActivityNewMessageBinding

class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding
    private var chatTopic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        chatTopic = intent.getStringExtra("topic_key").toString()
        supportActionBar?.title = chatTopic
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_message_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupAction() {

    }

}