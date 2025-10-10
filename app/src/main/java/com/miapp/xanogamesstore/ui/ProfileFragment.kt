package com.miapp.xanogamesstore.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.miapp.xanogamesstore.R

class ProfileFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View =
        i.inflate(R.layout.fragment_profile, c, false)

    override fun onViewCreated(v: View, s: Bundle?) {
        val tv = v.findViewById<TextView>(R.id.tvWelcome)
        val btn = v.findViewById<Button>(R.id.btnLogout)

        val name = SessionPrefs.getUsername(requireContext())
        tv.text = "¡Hola, ${if (name.isBlank()) "usuario" else name}!"

        btn.setOnClickListener {
            SessionPrefs.clear(requireContext())
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}