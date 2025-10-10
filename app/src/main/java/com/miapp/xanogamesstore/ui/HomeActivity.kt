package com.miapp.xanogamesstore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miapp.xanogamesstore.R

class HomeActivity : AppCompatActivity() {

    private fun show(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.commit {
            replace(R.id.homeContainer, fragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Fragment por defecto
        if (savedInstanceState == null) {
            show(ProfileFragment())
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> show(ProfileFragment())
                R.id.nav_products -> show(ProductsFragment())
                R.id.nav_add -> show(AddProductFragment())
            }
            true
        }
    }
}