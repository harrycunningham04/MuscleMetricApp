package com.cunninghamharry.loginactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class WorkoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Retrieve passed data
        val workoutName = intent.getStringExtra("workout_name") ?: "Workout"
        val exercises: ArrayList<Exercise> =
            intent.getParcelableArrayListExtra("exercises") ?: arrayListOf()
        val addExerciseButton: Button = findViewById(R.id.addExerciseButton)
        val completeWorkoutButton: Button = findViewById(R.id.completeWorkoutButton)

        // Set workout name
        val workoutTitle = findViewById<TextView>(R.id.workoutTitle)
        workoutTitle.text = workoutName

        // Back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set date
        val dateText = findViewById<TextView>(R.id.dateText)
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateText.text = "$currentDate • ${exercises.size} exercises"

        // Setup RecyclerView for exercises
        val recyclerView = findViewById<RecyclerView>(R.id.exerciseList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExerciseAdapter(exercises)
        Log.d("WorkoutActivity", "Exercises received: $exercises")

        addExerciseButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        completeWorkoutButton.setOnClickListener {
            // Save workout logic
            finish() // Go back to the previous screen
        }

    }
}
