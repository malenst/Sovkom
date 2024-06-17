package com.malenst.sovkom.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.malenst.sovkom.R
import com.malenst.sovkom.databinding.ActivityCoordinatorHomeBinding
import com.malenst.sovkom.model.User
import com.malenst.sovkom.ui.chat.ChatActivity
import com.malenst.sovkom.ui.user.UserAdapter

class CoordinatorHomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: CoordinatorHomeViewModel
    private var coordinatorId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_home)

        coordinatorId = intent.getLongExtra("USER_ID", -1)
        if (coordinatorId == -1L) {
            Toast.makeText(this, "Ошибка: ID координатора не найден.", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel = ViewModelProvider(this).get(CoordinatorHomeViewModel::class.java)

        setupRecyclerView()
        setupUI()

        viewModel.users.observe(this, Observer { users ->
            adapter.updateUsers(users ?: emptyList())
        })

        viewModel.loadExecutors(coordinatorId)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.executorsRecyclerView)
        adapter = UserAdapter(emptyList(), this::onChatClick, this::onItemClick, this::onAddClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupUI() {
        findViewById<FloatingActionButton>(R.id.fab_add_task).setOnClickListener {
            // Логика для добавления новой задачи
        }
    }

    private fun onChatClick(user: User) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("RECEIVER_ID", user.id)
        }
        startActivity(intent)
    }

    private fun onItemClick(user: User) {
        val intent = Intent(this, ExecutorHomeActivity::class.java).apply {
            putExtra("USER_ID", user.id)
            putExtra("AUTHORIZED_ID", 3L) // Передаем как Long
        }
        startActivity(intent)
    }

    private fun onAddClick(user: User) {
        // Логика для добавления задачи
    }
}



