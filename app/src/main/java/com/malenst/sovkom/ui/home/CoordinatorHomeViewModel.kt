package com.malenst.sovkom.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoordinatorHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    fun loadExecutors(coordinatorId: Long) {
        ApiClient.instance.getExecutorsByCoordinator(coordinatorId).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    _users.postValue(response.body())
                } else {
                    _users.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _users.postValue(emptyList())
            }
        })
    }
}