package com.cunninghamharry.loginactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExerciseSearchActivity : AppCompatActivity() {

    private lateinit var exerciseAdapter: ExerciseAdapter
    private val allExercises = mutableListOf<Exercise>() // Load from file
    private val filteredExercises = mutableListOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_search)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val recyclerView = findViewById<RecyclerView>(R.id.exerciseRecyclerView)

        // Load exercises from file (Replace with actual loading logic)
        loadExercises()

        exerciseAdapter = ExerciseAdapter(filteredExercises) { selectedExercise ->
            val resultIntent = Intent()
            resultIntent.putExtra("selected_exercise", selectedExercise)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = exerciseAdapter

        // Search filtering logic
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterExercises(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadExercises() {
        // Replace with your logic to load exercises from a file
        allExercises.add(Exercise("Bench Press", "Chest"))
        allExercises.add(Exercise("Squat", "Legs"))
        allExercises.add(Exercise("Deadlift", "Back"))

        filteredExercises.addAll(allExercises)
    }

    private fun filterExercises(query: String) {
        filteredExercises.clear()
        filteredExercises.addAll(allExercises.filter {
            it.name.contains(query, ignoreCase = true)
        })
        exerciseAdapter.notifyDataSetChanged()
    }
}