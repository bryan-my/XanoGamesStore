package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.miapp.xanogamesstore.R

/**
 * Versión base: muestra el email y el estado del token.
 * No hace llamadas de red; solo lee SessionPrefs.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTokenStatus: TextView = view.findViewById(R.id.tvTokenStatus)
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        val session = SessionPrefs(requireContext())

        // En la base solo mostramos si hay token o no.
        val token = session.authToken
        tvTokenStatus.text = if (token.isNullOrEmpty()) "Sin sesión" else "Sesión activa"

        // Si guardabas el email en prefs, puedes setearlo aquí.
        // En la base lo dejamos vacío o un placeholder.
        tvEmail.text = session.authToken?.let { "Usuario" } ?: "Invitado"

        btnLogout.setOnClickListener {
            session.authToken = null
            tvTokenStatus.text = "Sin sesión"
        }
    }
}