package com.miapp.xanogamesstore.ui

import android.content.Context

object SessionPrefs {
    private const val FILE = "session_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USERNAME = "username"
    private const val KEY_EMAIL = "email"

    fun saveToken(ctx: Context, token: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_TOKEN, token).apply()

    fun getToken(ctx: Context): String? =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)

    fun saveUsername(ctx: Context, name: String?) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_USERNAME, name ?: "").apply()

    fun getUsername(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_USERNAME, "") ?: ""

    fun saveEmail(ctx: Context, email: String) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_EMAIL, email).apply()

    fun getEmail(ctx: Context): String =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""

    fun clear(ctx: Context) =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().clear().apply()
}
