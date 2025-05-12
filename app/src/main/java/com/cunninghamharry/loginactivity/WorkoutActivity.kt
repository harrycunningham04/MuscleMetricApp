package com.cunninghamharry.loginactivity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    // Timer Variables
    private var workoutStartTime: Long = 0
    private var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable
    private lateinit var timerTextView: TextView

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

        val completeWorkoutButton: Button = findViewById(R.id.completeWorkoutButton)
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Set workout name
        val workoutTitle = findViewById<TextView>(R.id.workoutTitle)
        workoutTitle.text = workoutName

        // Timer TextView
        timerTextView = findViewById(R.id.timerText)

        // Start Timer when activity opens
        startTimer()

        // Back button
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

        completeWorkoutButton.setOnClickListener {
            stopTimer() // Stop and log workout
        }
    }

    // Start Timer
    private fun startTimer() {
        if (!isTimerRunning) {
            workoutStartTime = System.currentTimeMillis()
            isTimerRunning = true

            timerRunnable = object : Runnable {
                override fun run() {
                    val elapsedTime = System.currentTimeMillis() - workoutStartTime
                    val minutes = (elapsedTime / 1000) / 60
                    val seconds = (elapsedTime / 1000) % 60
                    timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                    handler.postDelayed(this, 1000) // Update every second
                }
            }

            handler.post(timerRunnable) // Start timer
        }
    }

    // Stop Timer and Log Workout
    private fun stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable)
            isTimerRunning = false

            val elapsedTime = System.currentTimeMillis() - workoutStartTime
            val minutes = (elapsedTime / 1000) / 60
            val seconds = (elapsedTime / 1000) % 60
            val duration = String.format("%02d:%02d", minutes, seconds)

            logWorkout(duration) // Save workout log
        }
    }

    // Log Workout
    private fun logWorkout(duration: String) {
        val workoutLog = StringBuilder()
        workoutLog.append("Workout Name: ${findViewById<TextView>(R.id.workoutTitle).text}\n")
        workoutLog.append("Date: ${findViewById<TextView>(R.id.dateText).text}\n")
        workoutLog.append("Duration: $duration\n")
        workoutLog.append("Exercises:\n")

        for (exercise in exercises) {
            workoutLog.append("  - ${exercise.name}:\n")
            for ((index, set) in exercise.sets.withIndex()) {
                workoutLog.append("    Set ${index + 1}: ${set.weight} kg x ${set.reps} reps\n")
            }
        }

        // Save the log (this could be to a database, file, or shared preferences)
        println(workoutLog.toString()) // Replace with actual logging mechanism
        finish() // Exit workout screen
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
                    sets = sets,
                )

                exercises.add(convertedExercise)
                exerciseAdapter.notifyItemInserted(exercises.size - 1)
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