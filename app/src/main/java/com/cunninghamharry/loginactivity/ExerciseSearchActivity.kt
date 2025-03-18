package com.cunninghamharry.loginactivity

import ExerciseSearchAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader


data class ExerciseNew(    val id: String,
                        val name: String,
                        val bodyPart: String,
                        val equipment: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(bodyPart)
        parcel.writeString(equipment)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExerciseNew> {
        override fun createFromParcel(parcel: Parcel): ExerciseNew = ExerciseNew(parcel)
        override fun newArray(size: Int): Array<ExerciseNew?> = arrayOfNulls(size)
    }
}

class ExerciseSearchActivity : AppCompatActivity() {

    private lateinit var exerciseAdapter: ExerciseSearchAdapter
    private val allExercises = mutableListOf<ExerciseNew>() // Load from JSON
    private val filteredExercises = mutableListOf<ExerciseNew>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_search)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val recyclerView = findViewById<RecyclerView>(R.id.exerciseRecyclerView)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Load exercises from JSON file
        loadExercises()

        exerciseAdapter = ExerciseSearchAdapter(filteredExercises) { selectedExercise ->
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
        try {
            val inputStream = assets.open("exercises.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val exercise = ExerciseNew(
                    id = jsonObject.getString("id"),
                    name = jsonObject.getString("name"),
                    bodyPart = jsonObject.getString("bodyPart"),
                    equipment = jsonObject.getString("equipment")
                )
                allExercises.add(exercise)
            }
            filteredExercises.addAll(allExercises) // Initially show all exercises
            exerciseAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterExercises(query: String) {
        filteredExercises.clear()
        filteredExercises.addAll(allExercises.filter {
            it.name.contains(query, ignoreCase = true)
        })
        exerciseAdapter.notifyDataSetChanged()
    }
}