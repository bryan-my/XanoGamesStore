package com.miapp.xanogamesstore.ui

import android.content.Context
import android.content.SharedPreferences

class SessionPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        set(value) { prefs.edit().putString("auth_token", value).apply() }

    var userRole: String?
        get() = prefs.getString("user_role", null)
        set(value) { prefs.edit().putString("user_role", value).apply() }
}