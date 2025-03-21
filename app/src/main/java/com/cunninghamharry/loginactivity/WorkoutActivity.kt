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
import android.os.Build

private const val REQUEST_CODE = 1

class WorkoutActivity : AppCompatActivity() {
    private lateinit var exercises: MutableList<Exercise>
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Retrieve passed data
        val workoutName = intent.getStringExtra("workout_name") ?: "Workout"
        exercises = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("exercises", Exercise::class.java)?.toMutableList() ?: mutableListOf()
        } else {
            intent.getParcelableArrayListExtra<Exercise>("exercises")?.toMutableList() ?: mutableListOf()
        }

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
        recyclerView = findViewById(R.id.exerciseList)
        exerciseAdapter = ExerciseAdapter(exercises) {
            updateExerciseCount()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

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
                val defaultSets = 3
                val defaultReps = 10

                val sets = mutableListOf<SetModel>()
                repeat(defaultSets) {
                    sets.add(SetModel(weight = 0.0, reps = defaultReps))
                }

                val convertedExercise = Exercise(
                    name = it.name,
                    sets = sets, // ✅ Use the correct sets list
                )

                exercises.add(convertedExercise)
                exerciseAdapter.notifyItemInserted(exercises.size - 1) // ✅ Efficient update
                updateExerciseCount()
            }
        }
    }

    private fun updateExerciseCount() {
        val dateText = findViewById<TextView>(R.id.dateText)
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateText.text = "$currentDate • ${exercises.size} exercises"
    }
}