package com.malenst.sovkom.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.malenst.sovkom.R
import com.malenst.sovkom.RetrofitBuilder
import com.malenst.sovkom.api.ApiClient
import com.malenst.sovkom.api.ApiService
import com.malenst.sovkom.databinding.ActivityLoginBinding
import com.malenst.sovkom.model.Credentials
import com.malenst.sovkom.model.User
import com.malenst.sovkom.ui.home.CoordinatorHomeActivity
import com.malenst.sovkom.ui.home.ExecutorHomeActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { ViewModelFactory(RetrofitBuilder.create()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            attemptLogin()
        }

        viewModel.loginResult.observe(this, Observer { loginResponse ->
            if (loginResponse != null && loginResponse.accessToken.isNotEmpty() && loginResponse.userId != 0L) {
                navigateToHome(loginResponse.role, loginResponse.userId)
            } else {
                Toast.makeText(this, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun attemptLogin() {
        val userId = binding.userIdEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val credentials = Credentials(username = userId, password = password)
        viewModel.login(credentials)
    }

    private fun navigateToHome(role: String, userId: Long) {
        val intent = when (role) {
            "EXECUTOR" -> Intent(this, ExecutorHomeActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            "COORDINATOR" -> Intent(this, CoordinatorHomeActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            else -> null
        }
        intent?.let {
            startActivity(it)
            finish()
        } ?: run {
            Toast.makeText(this, "Неопределенная роль или роль не поддерживается", Toast.LENGTH_SHORT).show()
        }
    }

}

