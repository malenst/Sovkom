package com.malenst.sovkom.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.api.ApiService
import com.malenst.sovkom.model.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class ExecutorHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks
    private val retrofitService = ApiClient.instance

    fun loadTasks(userId: Long, date: String) {
        retrofitService.getTasksForDate(userId, date).enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful && response.body() != null) {
                    _tasks.postValue(response.body())
                } else {
                    Log.e("ViewModel", "No tasks found or error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("ViewModel", "API call failed: ${t.message}")
            }
        })
    }

}


