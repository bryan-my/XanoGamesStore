package com.miapp.xanogamesstore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamestore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamestore.api.AuthService
import com.miapp.xanogamesstore.model.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay token guardado, ir directo a Home
        SessionPrefs.getToken(this)?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Retrofit SIN token para el login
        val retrofit = ApiClient.createRetrofit { null }
        val authService = retrofit.create(AuthService::class.java)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Corrutina en el ámbito de la Activity
            lifecycleScope.launch {
                try {
                    // Llamada en hilo de IO
                    val response = withContext(Dispatchers.IO) {
                        authService.login(LoginRequest(email, pass))
                    }

                    // Guardar sesión
                    SessionPrefs.saveToken(this@LoginActivity, response.token)
                    SessionPrefs.saveUsername(this@LoginActivity, response.user.name ?: response.user.email)
                    SessionPrefs.saveEmail(this@LoginActivity, response.user.email)

                    // Navegar
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
