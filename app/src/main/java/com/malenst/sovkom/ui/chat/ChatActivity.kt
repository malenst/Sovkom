package com.malenst.sovkom.ui.chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.model.Message
import com.malenst.sovkom.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private var receiverId: Long = 0
    private lateinit var buttonSend: ImageButton
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        recyclerView = findViewById(R.id.chatRecyclerView)
        val senderId = intent.getLongExtra("USER_ID", -1)
        receiverId = intent.getLongExtra("RECEIVER_ID", -1)
        setupRecyclerView()
        setupUI()
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        // API 21+
        editTextMessage.setHintTextColor(ContextCompat.getColor(this, R.color.my_hint_color))

        if (receiverId == -1L) {
            Toast.makeText(this, "Ошибка: ID получателя не найден.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            loadMessages(senderId, receiverId)
            loadReceiverDetails(receiverId)
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }




    private fun loadMessages(senderId: Long, receiverId: Long) {
        ApiClient.instance.getChatMessages(senderId, receiverId).enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful) {
                    adapter.updateMessages(response.body() ?: emptyList())
                } else if (response.code() == 204) {
                    Toast.makeText(this@ChatActivity, "No messages found", Toast.LENGTH_SHORT).show()
                    adapter.updateMessages(emptyList())
                } else {
                    Toast.makeText(this@ChatActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupUI() {
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(receiverId, intent.getLongExtra("RECEIVER_ID", -1), message)
                editTextMessage.text.clear()
            }
        }
    }

    private fun sendMessage(receiverId: Long, senderId: Long, messageText: String) {
        if (messageText.isNotEmpty()) {

            ApiClient.instance.sendMessage(receiverId, senderId, messageText).enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    if (response.isSuccessful) {

                        response.body()?.let { message ->
                            adapter.addMessage(message)
                            recyclerView.scrollToPosition(adapter.itemCount - 1)
                        }
                    } else {
                        Toast.makeText(this@ChatActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Toast.makeText(this@ChatActivity, "Error sending message: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadReceiverDetails(userId: Long) {
        ApiClient.instance.getUserDetails(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val userDetails = response.body()
                    userDetails?.let {
                        supportActionBar?.title = it.username
                    }
                } else {
                    Toast.makeText(this@ChatActivity, "Failed to load user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error loading user details: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}