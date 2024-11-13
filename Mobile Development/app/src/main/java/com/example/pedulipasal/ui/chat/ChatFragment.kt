package com.example.pedulipasal.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.pedulipasal.page.message.MessageActivity
import java.util.Date

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
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
        val chatList = ArrayList<ChatResponse>().apply {
            repeat(10) { chatIndex ->
                add(
                    ChatResponse(
                        chatId = "chat${chatIndex + 1}",
                        userId = "user${chatIndex + 1}",
                        title = "Chat Title ${chatIndex + 1}",
                        createdAt = Date(),
                        updateAt = Date(),
                        messages = List(10) { messageIndex ->
                            Message(
                                messageId = "msg${chatIndex + 1}_${messageIndex + 1}",
                                isByHuman = messageIndex % 2 == 0, // Alternate between human and bot
                                content = "Message content ${messageIndex + 1} from Chat ${chatIndex + 1}",
                                timestamp = Date()
                            )
                        }
                    )
                )
            }
        }

        chatAdapter = ChatAdapter(requireActivity(), chatList, object : ChatAdapter.OnItemSelected {
            override fun onItemClicked(chatId: String?, messageList: List<Message?>?) {
                Toast.makeText(requireActivity(), "chat id: $chatId", Toast.LENGTH_SHORT).show()
            }
        })

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = chatAdapter
        }
    }

    private fun setupAction() {
        binding.fabAddNewChat.setOnClickListener { showDialog(requireActivity()) }
    }

    private fun showDialog(context: Context) {
        val inflater = LayoutInflater.from(context)
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)

        AlertDialog.Builder(context).apply {
            setTitle("Add new chat")
            setView(dialogLayout)
            setPositiveButton("Create") {_, _, ->
                val text = dialogLayout.findViewById<EditText>(R.id.ed_new_topics).text.toString()
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
            create()
            show()
        }

    }

    private fun moveToMessageActivity() {
        val intent = Intent(requireActivity(), MessageActivity::class.java)
        startActivity(intent)
    }


}