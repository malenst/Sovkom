package com.malenst.sovkom.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.malenst.sovkom.ui.home.CoordinatorHomeActivity
import com.malenst.sovkom.ui.home.ExecutorHomeActivity
import com.malenst.sovkom.ui.login.LoginActivity
import com.malenst.sovkom.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            val role = sessionManager.getUserRole()
            when (role) {
                "COORDINATOR" -> startActivity(Intent(this, CoordinatorHomeActivity::class.java))
                "EXECUTOR" -> startActivity(Intent(this, ExecutorHomeActivity::class.java))
                else -> startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}