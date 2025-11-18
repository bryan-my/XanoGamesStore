package com.miapp.xanogamesstore.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.AuthService
import com.miapp.xanogamesstore.api.UserService
import com.miapp.xanogamesstore.model.SignupBody
import com.miapp.xanogamesstore.model.UpdateUserBody
import com.miapp.xanogamesstore.model.UserDto
import com.miapp.xanogamesstore.ui.adapter.UserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class UsersFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recycler = view.findViewById(R.id.rvUsers)
        fabAdd = view.findViewById(R.id.fabAddUser)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(mutableListOf(),
            onEdit = { user -> editUser(user) },
            onBlockToggle = { user -> toggleBlock(user) },
            onDelete = { user -> deleteUser(user) }
        )
        recycler.adapter = adapter
        fabAdd.setOnClickListener { showCreateUserDialog() }
        return view
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun loadUsers() {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(UserService::class.java)
            try {
                delay(1500L)
                val users = withContext(Dispatchers.IO) { api.getUsers() }
                adapter.replaceAll(users)
            } catch (e: Exception) {
                val msg = e.message ?: "Error al cargar usuarios"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showCreateUserDialog() {
        val ctx = requireContext()
        val inflater = LayoutInflater.from(ctx)
        val dialogView = inflater.inflate(R.layout.dialog_create_user, null)
        val etEmail: EditText = dialogView.findViewById(R.id.etEmail)
        val etName: EditText = dialogView.findViewById(R.id.etName)
        val etPassword: EditText = dialogView.findViewById(R.id.etPassword)

        AlertDialog.Builder(ctx)
            .setTitle(R.string.title_users)
            .setView(dialogView)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val email = etEmail.text.toString().trim()
                val name = etName.text.toString().trim().ifEmpty { null }
                val password = etPassword.text.toString().trim()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ctx, "Email y contraseña son obligatorios", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                createUser(email, password, name)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun createUser(email: String, password: String, name: String?) {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.auth(ctx).create(AuthService::class.java)
            try {
                withContext(Dispatchers.IO) {
                    api.signup(SignupBody(email, password, name))
                }
                Toast.makeText(ctx, "Usuario creado", Toast.LENGTH_SHORT).show()
                loadUsers()
            } catch (e: Exception) {
                val msg = e.message ?: "Error al crear usuario"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun editUser(user: UserDto) {
        val ctx = requireContext()
        val inflater = LayoutInflater.from(ctx)
        val dialogView = inflater.inflate(R.layout.dialog_edit_user, null)
        val etEmail: EditText = dialogView.findViewById(R.id.etEmail)
        val etName: EditText = dialogView.findViewById(R.id.etName)
        etEmail.setText(user.email)
        etName.setText(user.name ?: "")
        AlertDialog.Builder(ctx)
            .setTitle(getString(R.string.action_edit))
            .setView(dialogView)
            .setPositiveButton(R.string.action_save) { _, _ ->
                val newEmail = etEmail.text.toString().trim()
                val newName = etName.text.toString().trim().ifEmpty { null }
                if (newEmail.isEmpty()) {
                    Toast.makeText(ctx, "El email no puede estar vacío", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                updateUser(user.id, newEmail, newName)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun updateUser(id: Int, email: String, name: String?) {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(UserService::class.java)
            try {
                withContext(Dispatchers.IO) {
                    api.updateUser(id, UpdateUserBody(name, email))
                }
                Toast.makeText(ctx, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                loadUsers()
            } catch (e: Exception) {
                val msg = e.message ?: "Error al actualizar usuario"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleBlock(user: UserDto) {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(UserService::class.java)
            try {
                val currentStatus = user.active != false
                val newStatus = !currentStatus
                withContext(Dispatchers.IO) {
                    api.updateUser(user.id, UpdateUserBody(user.name, user.email, newStatus))
                }
                val message = if (newStatus) "Usuario activado" else "Usuario bloqueado"
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                loadUsers()
            } catch (e: Exception) {
                val msg = e.message ?: "Error al cambiar estado"
                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteUser(user: UserDto) {
        val ctx = requireContext()
        AlertDialog.Builder(ctx)
            .setTitle(getString(R.string.action_delete))
            .setMessage("¿Seguro que deseas eliminar a ${user.email}?")
            .setPositiveButton(R.string.action_delete) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val api = ApiClient.shop(ctx).create(UserService::class.java)
                    try {
                        withContext(Dispatchers.IO) { api.deleteUser(user.id) }
                        Toast.makeText(ctx, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } catch (e: Exception) {
                        val msg = e.message ?: "Error al eliminar usuario"
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
