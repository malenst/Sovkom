package com.malenst.sovkom.ui.chat

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.model.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: ChatAdapter
    private var receiverId: Long = 0
    private lateinit var buttonSend: Button
    private lateinit var editTextMessage: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        // API 21+
        editTextMessage.setHintTextColor(ContextCompat.getColor(this, R.color.my_hint_color))
        val receiverId = intent.getLongExtra("RECEIVER_ID", -1)
        if (receiverId == -1L) {
            Toast.makeText(this, "Ошибка: ID получателя не найден.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(emptyList())
        findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = this@ChatActivity.adapter
        }
    }

    private fun setupChat(coordinatorId: Long) {
        // Инициализация UI и загрузка сообщений
        loadMessages(coordinatorId)
    }



    private fun loadMessages(receiverId: Long) {
        ApiClient.instance.getChatMessages(receiverId).enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful) {
                    adapter.updateMessages(response.body() ?: emptyList())
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
    }

    private fun sendMessage(receiverId: Long, senderId: Long, message: String) {
        // Реализация отправки сообщения
        ApiClient.instance.sendMessage(receiverId, senderId, message).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    loadMessages(receiverId)  // Перезагрузить сообщения после отправки
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@ChatActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}