package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.UserDto

/**
 * Adaptador para mostrar una lista de usuarios en la vista de administración.
 * Incluye callbacks para acciones de editar, bloquear/desbloquear y eliminar.
 */
class UserAdapter(
    private val users: MutableList<UserDto>,
    private val onEdit: (UserDto) -> Unit,
    private val onBlockToggle: (UserDto) -> Unit,
    private val onDelete: (UserDto) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(users[position])
    }

    fun replaceAll(newUsers: List<UserDto>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditUser)
        private val btnBlock: Button = itemView.findViewById(R.id.btnBlockUser)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteUser)

        fun bind(user: UserDto) {
            tvName.text = user.name ?: itemView.context.getString(R.string.user_generic)
            tvEmail.text = user.email
            // Ajusta el texto del botón de bloqueo según campo disponible. Si no existe,
            // siempre mostrará "Bloquear". Puedes personalizarlo según tu modelo.
            btnBlock.text = itemView.context.getString(R.string.action_block)

            btnEdit.setOnClickListener { onEdit(user) }
            btnBlock.setOnClickListener { onBlockToggle(user) }
            btnDelete.setOnClickListener { onDelete(user) }
        }
    }
}