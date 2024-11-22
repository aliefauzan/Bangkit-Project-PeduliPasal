package com.example.pedulipasal.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pedulipasal.R
import com.example.pedulipasal.adapter.ChatAdapter
import com.example.pedulipasal.data.model.request.CreateChatRequest
import com.example.pedulipasal.databinding.FragmentChatBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.helper.observeOnce
import com.example.pedulipasal.page.message.MessageActivity

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    companion object {
        private const val CHAT_ID_KEY = "detail_chat_key"
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
        chatViewModel.getSession().observe(viewLifecycleOwner) {user ->
            getHistoryChat(user.userId)
        }
    }

    private fun getHistoryChat(userId: String) {
        chatViewModel.getUserHistoryChat(userId).observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        chatAdapter = ChatAdapter(requireActivity(), result.data.sortedByDescending { it.createdAt }, object : ChatAdapter.OnItemSelected {
                            override fun onItemClicked(chatId: String) {
                                moveToMessageActivity(chatId = chatId)
                            }

                            override fun onButtonDeleteClick(chatId: String) {
                                showDeleteDialog(chatId)
                            }
                        })
                        binding.rvChats.apply {
                            layoutManager = LinearLayoutManager(requireActivity())
                            adapter = chatAdapter
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), "Gagal mengambil data chat ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    private fun moveToMessageActivity(chatId: String) {
        val intent = Intent(requireActivity(), MessageActivity::class.java).apply {
            putExtra(CHAT_ID_KEY, chatId)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(chatId: String) {
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.delete_chat))
            setMessage(R.string.delete_chat_confirmation)
            setPositiveButton(R.string.confirm) { _, _ ->
                chatViewModel.deleteChatById(chatId).observe(viewLifecycleOwner) {result ->
                    if (result != null) {
                        when(result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                chatAdapter.deleteItem(chatId)
//                                Toast.makeText(requireActivity(), "Berhasil menghapus chat", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                            }
                            is Result.Error -> {
                                Toast.makeText(requireActivity(), "Gagal menghapus chat dengan id ${result.error}", Toast.LENGTH_SHORT).show()
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    private fun setupAction() {
        binding.fabAddNewChat.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val inflater = LayoutInflater.from(requireActivity())
        val dialogLayout = inflater.inflate(R.layout.dialog_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.ed_new_topics)

        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.add_new_chat))
            setView(dialogLayout)
            setPositiveButton(R.string.create) { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    chatViewModel.getSession().observeOnce(viewLifecycleOwner) {user ->
                        createNewChat(title = title, userId = user.userId)
                    }
                } else {
                    Toast.makeText(context, getString(R.string.empty_topic_warning), Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton(R.string.cancel, null)
            create()
            show()
        }
    }

    private fun createNewChat(title: String, userId: String) {
        val createChatRequest = CreateChatRequest (
            userId = userId,
            title = title
        )
        chatViewModel.createChat(createChatRequest).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        result.data.chatId?.let { moveToMessageActivity(it) }
                        //Log.d("ChatActivity", "Berhasil membuat chat baru dengan id ${result.data.chatId}")
//                        Toast.makeText(requireActivity(), "Berhasil membuat chat baru dengan id ${result.data.chatId}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        //Log.d("ChatActivity", "Gagal membuat chat baru dengan id ${result.error}")
                        Toast.makeText(requireActivity(), "Gagal membuat chat baru dengan id ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initializeData()
    }
}
