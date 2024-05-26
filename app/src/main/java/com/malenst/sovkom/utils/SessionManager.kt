package com.malenst.sovkom.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IsLoggedIn", false)
    }

    fun getUserRole(): String? {
        return prefs.getString("UserRole", null)
    }

    // Добавьте здесь методы для сохранения и очистки учетных данных
}