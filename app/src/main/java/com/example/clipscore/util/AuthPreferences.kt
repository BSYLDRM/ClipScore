package com.example.clipscore.util

import android.content.Context

class AuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("clipscore_auth", Context.MODE_PRIVATE)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) { prefs.edit().putBoolean("is_logged_in", value).apply() }

    var userEmail: String
        get() = prefs.getString("user_email", "") ?: ""
        set(value) { prefs.edit().putString("user_email", value).apply() }

    fun saveSession(email: String) {
        isLoggedIn = true
        userEmail = email
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
