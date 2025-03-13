package com.cunninghamharry.loginactivity

import HistoryFragment
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val pageTitle = findViewById<TextView>(R.id.pageTitle)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        // Load the default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PlansFragment())
            .commit()

        // Handle bottom navigation clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_plans -> {
                    pageTitle.text = "My Workout Plans"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PlansFragment())
                        .commit()
                    true
                }
                R.id.nav_history -> {
                    pageTitle.text = "History"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HistoryFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Handle logout button click
        btnLogout.setOnClickListener {
            val intent = Intent(this@HomePage, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clears back stack
            startActivity(intent)
        }
    }
}
