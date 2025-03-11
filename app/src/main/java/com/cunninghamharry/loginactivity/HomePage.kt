package com.cunninghamharry.loginactivity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val pageTitle = findViewById<TextView>(R.id.pageTitle)

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
    }
}
