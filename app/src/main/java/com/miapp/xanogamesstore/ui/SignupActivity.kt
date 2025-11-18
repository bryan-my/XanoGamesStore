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
import com.miapp.xanogamesstore.model.SignupBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var progress: ProgressBar
    private lateinit var session: SessionPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        session = SessionPrefs(this)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnSignup)
        progress = findViewById(R.id.progress)

        btnSignup.setOnClickListener { doSignup() }
    }

    private fun doSignup() {
        val email = etEmail.text.toString().trim()
        val pass  = etPassword.text.toString()
        val name  = etName.text.toString().takeIf { it.isNotBlank() }

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa email y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        btnSignup.isEnabled = false
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = ApiClient.auth(this@SignupActivity).create(AuthService::class.java)
                
                // 1. Registro y obtención del token
                val resp = withContext(Dispatchers.IO) {
                    api.signup(SignupBody(email, pass, name))
                }
                session.authToken = resp.authToken

                // 2. OBLIGATORIO: Cargar los datos del usuario recién creado para tener su ID
                val userDto = withContext(Dispatchers.IO) {
                    api.me()
                }
                session.userId = userDto.id.toString()
                // session.userRole = userDto.role 

                // Navegar al Home
                startActivity(Intent(this@SignupActivity, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SignupActivity, e.message ?: "Error al crear cuenta", Toast.LENGTH_LONG).show()
                session.authToken = null
                session.userId = null
            } finally {
                progress.visibility = View.GONE
                btnSignup.isEnabled = true
            }
        }
    }
}
