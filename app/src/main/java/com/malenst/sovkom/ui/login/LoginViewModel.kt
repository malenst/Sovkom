package com.malenst.sovkom.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.api.ApiService
import com.malenst.sovkom.model.Credentials
import com.malenst.sovkom.model.LoginResponse
import com.malenst.sovkom.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val apiService: ApiService) : ViewModel() {
    val loginResult = MutableLiveData<LoginResponse?>()

    fun login(credentials: Credentials) {
        viewModelScope.launch {
            try {
                val response = apiService.loginUser(credentials)
                if (response.isSuccessful && response.body() != null) {
                    loginResult.postValue(response.body())
                } else {
                    Log.e("LoginActivity", "Error logging in: ${response.errorBody()?.string()}")
                    loginResult.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error logging in: ${e.message}")
                loginResult.postValue(null)
            }
        }
    }
}