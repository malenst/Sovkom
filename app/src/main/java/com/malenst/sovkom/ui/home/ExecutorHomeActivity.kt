package com.malenst.sovkom.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.RetrofitBuilder
import com.malenst.sovkom.databinding.ActivityExecutorHomeBinding
import com.malenst.sovkom.model.Task
import com.malenst.sovkom.ui.login.ViewModelFactory
import com.malenst.sovkom.ui.task.TasksAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExecutorHomeActivity : AppCompatActivity() {

    private lateinit var viewModel: ExecutorHomeViewModel
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var weekDaysContainer: LinearLayout
    private var selectedDate: Date = Date()  // Сегодняшняя дата изначально выбрана
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = formatter.format(selectedDate)
    private var userId: Long = -1 // ID пользователя

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_executor_home)

        userId = intent.getLongExtra("USER_ID", -1)
        if (userId == -1L) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            viewModel = ViewModelProvider(this)[ExecutorHomeViewModel::class.java]
            setupRecyclerView()
            setupDateSelection()
            updateTasksForSelectedDate()  // Теперь запрос отправляется после успешной установки userId
        }

        viewModel.tasks.observe(this, Observer { tasks ->
            tasksAdapter.updateTasks(tasks ?: emptyList())
        })
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.tasksRecyclerView)
        tasksAdapter = TasksAdapter(emptyList())
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDateSelection() {
        weekDaysContainer = findViewById(R.id.weekDaysContainer)
        findViewById<Button>(R.id.btnPrevious).setOnClickListener {
            changeWeek(-1)
        }
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            changeWeek(1)
        }
        displayWeekDays()
    }

    private fun displayWeekDays() {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        weekDaysContainer.removeAllViews()

        for (i in 0 until 7) {
            val dayButton = Button(this).apply {
                text = calendar.get(Calendar.DAY_OF_MONTH).toString()
                setOnClickListener {
                    selectedDate = calendar.time
                    updateTasksForSelectedDate()
                }
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                if (formatter.format(calendar.time) == formatter.format(selectedDate)) {
                    setBackgroundResource(R.drawable.selected_day_background)
                }
            }
            weekDaysContainer.addView(dayButton)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.time = selectedDate // Возвращаем календарь к выбранной дате
    }

    private fun changeWeek(weekOffset: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
        selectedDate = calendar.time
        displayWeekDays()
    }

    private fun updateTasksForSelectedDate() {
        if (userId == -1L) {
            Toast.makeText(this, "Ошибка: ID пользователя не найден.", Toast.LENGTH_SHORT).show()
            return
        }
        val formattedDate = formatter.format(selectedDate) // Форматируем дату
        viewModel.loadTasks(userId, formattedDate)
    }
}
