package com.malenst.sovkom.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.malenst.sovkom.databinding.ActivityCoordinatorHomeBinding

class CoordinatorHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoordinatorHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinatorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddTask.setOnClickListener {
            // Логика для перехода к форме создания задачи
        }

        binding.btnNotifications.setOnClickListener {
            // Логика для открытия уведомлений
        }

        binding.btnChat.setOnClickListener {
            // Логика для открытия чата
        }

        // Здесь должен быть код для заполнения списка исполнителей
    }
}