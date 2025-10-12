package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import android.content.Intent
import android.widget.Button

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var btnLogout: Button
    private lateinit var session: SessionPrefs
    private lateinit var tvEmail: TextView
    private lateinit var progress: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)
        tvName = v.findViewById(R.id.tvName)
        tvEmail = v.findViewById(R.id.tvEmail)
        progress = v.findViewById(R.id.progress)
        btnLogout = v.findViewById(R.id.btnLogout)
        session = SessionPrefs(requireContext())

        btnLogout.setOnClickListener { logout() }
        return v
    }

    override fun onStart() {
        super.onStart()
        loadMe()
    }

    private fun loadMe() {
        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.auth(requireContext()).create(AuthService::class.java)
                val me = withContext(Dispatchers.IO) { api.me() }
                tvName.text = me.name ?: "(Sin nombre)"
                tvEmail.text = me.email
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
                    // aquí si quieres navega a LoginActivity
                } else {
                    Toast.makeText(requireContext(), e.message ?: "Error al cargar perfil", Toast.LENGTH_LONG).show()
                }
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
    private fun logout() {
        // borra credenciales
        session.authToken = null

        // envía a Login y limpia el back stack
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
