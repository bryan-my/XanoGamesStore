package com.miapp.xanogamesstore.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.AuthService
import com.miapp.xanogamesstore.model.LoginBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar
    private lateinit var session: SessionPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = SessionPrefs(this)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)

        btnLogin.setOnClickListener { doLogin() }
    }

    private fun doLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
            return
        }

        btnLogin.isEnabled = false
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = ApiClient.auth(this@LoginActivity).create(AuthService::class.java)
                val resp = withContext(Dispatchers.IO) {
                    api.login(LoginBody(email, password))
                }
                session.authToken = resp.authToken  // guarda token

                startActivity(
                    Intent(this@LoginActivity, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnLogin.isEnabled = true
            }
        }
    }
}