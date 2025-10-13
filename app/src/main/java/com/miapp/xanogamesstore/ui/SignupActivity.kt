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
import com.miapp.xanogamesstore.ui.SessionPrefs
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
                val resp = withContext(Dispatchers.IO) {
                    api.signup(SignupBody(email, pass, name))
                }
                // guarda token y navega como en login
                session.authToken = resp.authToken

                // opcional: cargar /auth/me para rol y redirigir
                startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SignupActivity, e.message ?: "Error al crear cuenta", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnSignup.isEnabled = true
            }
        }
    }
}