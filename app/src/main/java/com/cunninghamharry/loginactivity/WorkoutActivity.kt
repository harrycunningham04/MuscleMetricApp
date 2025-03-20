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

private const val REQUEST_CODE = 1

class WorkoutActivity : AppCompatActivity() {
    private lateinit var exercises: ArrayList<Exercise>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Retrieve passed data
        val workoutName = intent.getStringExtra("workout_name") ?: "Workout"
        exercises = intent.getParcelableArrayListExtra("exercises") ?: arrayListOf() // No 'val' here

        val addExerciseButton: Button = findViewById(R.id.addExerciseButton)
        val completeWorkoutButton: Button = findViewById(R.id.completeWorkoutButton)

        // Set workout name
        val workoutTitle = findViewById<TextView>(R.id.workoutTitle)
        workoutTitle.text = workoutName

        // Back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        // Set date
        val dateText = findViewById<TextView>(R.id.dateText)
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateText.text = "$currentDate • ${exercises.size} exercises"

        // Setup RecyclerView for exercises
        recyclerView = findViewById(R.id.exerciseList) // Assign to class-level variable
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExerciseAdapter(exercises) { selectedExercise ->
            Log.d("WorkoutActivity", "Clicked on ${selectedExercise.name}")
        }

        addExerciseButton.setOnClickListener {
            val intent = Intent(this, ExerciseSearchActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        completeWorkoutButton.setOnClickListener {
            // Save workout logic
            finish() // Go back to the previous screen
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val newExerciseNew = data?.getParcelableExtra<ExerciseNew>("selected_exercise")
            newExerciseNew?.let {
                val defaultSets = 3 // You can modify this per exercise
                val defaultReps = 10 // You can modify this per exercise

                val sets = mutableListOf<SetModel>()
                repeat(defaultSets) {
                    sets.add(SetModel(weight = 0.0, reps = defaultReps))
                }

                val convertedExercise = Exercise(
                    name = it.name,
                    sets = sets.size,
                    reps = defaultReps,
                    weight = 0.0
                )

                exercises.add(convertedExercise)
                findViewById<RecyclerView>(R.id.exerciseList).adapter?.notifyDataSetChanged()
            }
        }
    }
}
