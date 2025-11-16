package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.miapp.xanogamesstore.core.Roles

class HomeActivity : AppCompatActivity() {

    private lateinit var bottom: BottomNavigationView
    private lateinit var session: SessionPrefs
    private lateinit var toolbar: com.google.android.material.appbar.MaterialToolbar

    private fun show(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.commit {
            replace(R.id.homeContainer, fragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        session = SessionPrefs(this)
        bottom = findViewById(R.id.bottomNav)
        toolbar = findViewById(R.id.toolbar)

        // 1) Traer role de /auth/me, normalizar y guardar
        lifecycleScope.launch {
            val api = ApiClient.auth(this@HomeActivity).create(AuthService::class.java)
            val me  = withContext(Dispatchers.IO) { api.me() }
            val roleGot = (me.role ?: "customer").trim().lowercase(Locale.US)
            session.userRole = roleGot

            Toast.makeText(this@HomeActivity, "role=$roleGot", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "role from /auth/me = '$roleGot'")

            // 2) Armar bottom según role
            setupBottom(roleGot)
        }
    }

    private fun showTitle(title: String) { toolbar.title = title }

    private fun setupBottom(role: String) {
        Log.d("HomeActivity", "setupBottom with role='$role'")
        bottom.menu.clear()

        if (role.equals(Roles.ADMIN, ignoreCase = true)) {
            bottom.inflateMenu(R.menu.bottom_admin)
            Log.d("HomeActivity", "Inflated: bottom_admin")
        } else {
            bottom.inflateMenu(R.menu.bottom_customer)
            Log.d("HomeActivity", "Inflated: bottom_customer")
        }

        // Fragment inicial
        show(ProfileFragment())
        showTitle(getString(R.string.title_profile))

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile  -> { show(ProfileFragment());    showTitle(getString(R.string.title_profile)) }
                R.id.nav_products -> { show(ProductsFragment());   showTitle(getString(R.string.title_products)) }
                R.id.nav_add      -> { show(AddProductFragment()); showTitle(getString(R.string.title_add)) }     // admin
                R.id.nav_cart     -> { show(CartFragment());       showTitle(getString(R.string.title_cart)) }
                R.id.nav_orders   -> {
                    show(OrdersFragment())
                    showTitle(getString(R.string.title_orders))
                }// customer
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        // Debug: lista los items cargados
        for (i in 0 until bottom.menu.size()) {
            Log.d("HomeActivity", "menuItem[$i]=${bottom.menu.getItem(i).title}")
        }
    }

}
