package com.example.pedulipasal.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.ChatAdapter
import com.example.pedulipasal.data.model.ChatResponse
import com.example.pedulipasal.data.model.Message
import com.example.pedulipasal.databinding.FragmentChatBinding
import com.example.pedulipasal.page.chat.ChatActivity
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TOPIC_KEY = "topic_key"
        private const val DETAIL_CHAT_KEY = "detail_chat_key"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeData()
        setupAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeData() {
        val chatList = generateMockChatData()

        chatAdapter = ChatAdapter(requireActivity(), chatList, object : ChatAdapter.OnItemSelected {
            override fun onItemClicked(chatResponse: ChatResponse) {
                moveToChatActivity(chatResponse = chatResponse)
            }
        })

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = chatAdapter
        }
    }

    private fun generateMockChatData(): List<ChatResponse> {
        val chatList = mutableListOf<ChatResponse>()
        val calendar = Calendar.getInstance()

        for (chatIndex in 1..10) {
            calendar.add(Calendar.DAY_OF_YEAR, -chatIndex)

            val messages = mutableListOf<Message>()
            for (messageIndex in 1..30) {
                calendar.add(Calendar.MINUTE, -messageIndex)
                messages.add(
                    Message(
                        messageId = "msg${chatIndex}_$messageIndex",
                        isByHuman = messageIndex % 2 == 0,
                        content = "Message content $messageIndex from Chat $chatIndex",
                        timestamp = calendar.time
                    )
                )
            }

            chatList.add(
                ChatResponse(
                    chatId = "chat$chatIndex",
                    userId = "user$chatIndex",
                    title = "Chat Title $chatIndex",
                    createdAt = calendar.time,
                    updateAt = Date(),
                    messages = messages
                )
            )
        }
        return chatList
    }
    private fun setupAction() {
        binding.fabAddNewChat.setOnClickListener {
            showDialog(requireActivity())
        }
    }

    private fun showDialog(context: Context) {
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.ed_new_topics)

        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.add_new_chat))
            setView(dialogLayout)
            setPositiveButton(R.string.create) { _, _ ->
                val topic = editText.text.toString().trim()
                if (topic.isNotEmpty()) {
                    moveToChatActivity(topic = topic)
                } else {
                    Toast.makeText(context, getString(R.string.empty_topic_warning), Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    private fun moveToChatActivity(topic: String? = null, chatResponse: ChatResponse? = null) {
        val intent = Intent(requireActivity(), ChatActivity::class.java).apply {
            topic?.let { putExtra(TOPIC_KEY, it) }
            chatResponse?.let { putExtra(DETAIL_CHAT_KEY, it) }
        }
        startActivity(intent)
    }
}
