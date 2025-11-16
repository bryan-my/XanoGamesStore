package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.AuthService
import com.miapp.xanogamesstore.api.UserService
import com.miapp.xanogamesstore.api.extractHttpError
import com.miapp.xanogamesstore.model.UpdateUserBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Fragmento para editar los datos personales del usuario.
 * Permite modificar el nombre y el correo electrónico.
 */
class EditProfileFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var progress: ProgressBar
    private lateinit var session: SessionPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        progress = view.findViewById(R.id.progressEdit)
        session = SessionPrefs(requireContext())

        loadCurrentUser()

        btnSave.setOnClickListener { saveChanges() }
        btnCancel.setOnClickListener {
            // Volver al fragmento anterior
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    /**
     * Carga la información actual del usuario desde el endpoint auth/me
     * y la coloca en los campos de texto.
     */
    private fun loadCurrentUser() {
        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.auth(requireContext()).create(AuthService::class.java)
                val me = withContext(Dispatchers.IO) { api.me() }
                etName.setText(me.name ?: "")
                etEmail.setText(me.email)
            } catch (e: Exception) {
                val msg = if (e is HttpException && e.code() == 401) {
                    "Sesión expirada. Inicia sesión de nuevo."
                } else {
                    e.message ?: "Error al cargar datos del usuario"
                }
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    /**
     * Valida los campos y envía la actualización al backend.
     */
    private fun saveChanges() {
        val ctx = requireContext()
        val userId = session.userId?.toIntOrNull()
        if (userId == null) {
            Toast.makeText(ctx, "No se ha encontrado el usuario actual", Toast.LENGTH_LONG).show()
            return
        }
        val name = etName.text?.toString()?.trim()
        val email = etEmail.text?.toString()?.trim()
        if (email.isNullOrBlank()) {
            Toast.makeText(ctx, "El correo no puede estar vacío", Toast.LENGTH_LONG).show()
            return
        }
        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.shop(ctx).create(UserService::class.java)
                withContext(Dispatchers.IO) {
                    api.updateUser(userId, UpdateUserBody(name, email))
                }
                Toast.makeText(ctx, "Datos actualizados", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendlyMsg =
                    if (e is HttpException && e.code() == 429)
                        "Demasiadas peticiones, intenta de nuevo más tarde"
                    else msg
                Toast.makeText(ctx, "Error al actualizar datos: $friendlyMsg", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
