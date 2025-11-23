package com.miapp.xanogamesstore.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var tvCreate: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar
    private lateinit var session: SessionPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)
        tvCreate = findViewById(R.id.tvCreateAccount)
        
        session = SessionPrefs(this)

        // Configurar Listeners
        tvCreate.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        btnLogin.setOnClickListener { doLogin() }

        // --- VERIFICACIÓN DE SESIÓN AL INICIO ---
        checkSessionOnStart()
    }

    private fun checkSessionOnStart() {
        // 1. Validación básica local: ¿Tenemos token y ID?
        if (session.authToken.isNullOrEmpty() || session.userId.isNullOrEmpty()) {
            // Sesión inexistente o incompleta -> El usuario debe loguearse
            return
        }

        // 2. Validación con el servidor (Token Expiration)
        // Ocultamos botones y mostramos carga mientras verificamos
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val api = ApiClient.auth(this@LoginActivity).create(AuthService::class.java)
                
                // Llamamos a /auth/me para ver si el token sigue vivo
                val userDto = withContext(Dispatchers.IO) { api.me() }

                // Si llegamos aquí, el token es válido.
                // Actualizamos datos por si acaso (ej. el rol cambió)
                session.userId = userDto.id.toString()
                session.userRole = userDto.role

                // ¡Todo bien! Vamos al Home
                goToHome()

            } catch (e: Exception) {
                // Si falla (Token expirado, 401, o sin internet/servidor caído)
                // Asumimos sesión inválida para evitar crashes y pedimos login
                
                // Limpiamos la sesión para que no estorbe
                session.authToken = null
                session.userId = null
                session.userRole = null
                
                Toast.makeText(this@LoginActivity, "Sesión expirada o error de conexión", Toast.LENGTH_SHORT).show()
                
                // Restauramos la interfaz para permitir login manual
                setLoadingState(false)
            }
        }
    }

    private fun doLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa email y password", Toast.LENGTH_SHORT).show()
            return
        }

        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val api = ApiClient.auth(this@LoginActivity).create(AuthService::class.java)
                
                // 1. Login para obtener el token
                val resp = withContext(Dispatchers.IO) {
                    api.login(LoginBody(email, password))
                }
                
                session.authToken = resp.authToken 

                // 2. Obtener los datos del usuario (ID, Rol, etc.)
                val userDto = withContext(Dispatchers.IO) {
                    api.me()
                }

                // 3. Guardamos los datos críticos en la sesión
                session.userId = userDto.id.toString()
                session.userRole = userDto.role

                goToHome()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                
                // En caso de error al loguear, limpiamos por seguridad
                session.authToken = null
                session.userId = null
                session.userRole = null
                
                setLoadingState(false)
            }
        }
    }

    private fun goToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            progress.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE
            tvCreate.visibility = View.INVISIBLE
            etEmail.isEnabled = false
            etPassword.isEnabled = false
        } else {
            progress.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
            tvCreate.visibility = View.VISIBLE
            etEmail.isEnabled = true
            etPassword.isEnabled = true
        }
    }
}
