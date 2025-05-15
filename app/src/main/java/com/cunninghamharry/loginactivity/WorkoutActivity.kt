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
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

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
    private var workoutId: Int = 0
    private var workoutHistoryId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Retrieve passed data
        workoutId = intent.getIntExtra("workout_id", 0)
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
            stopTimer()
            completeWorkoutButton.isEnabled = false
            finish()
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

            val hours = (elapsedTime / 1000) / 3600
            val minutes = ((elapsedTime / 1000) % 3600) / 60
            val seconds = (elapsedTime / 1000) % 60

            // Format the duration in HH:MM:SS
            val duration = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            logWorkout(duration)
        }
    }


    private fun logWorkout(duration: String) {
        // Build the workout log to be displayed (for debugging or review)
        val workoutLog = StringBuilder()
        workoutLog.append("Workout Name: ${findViewById<TextView>(R.id.workoutTitle).text}\n")
        workoutLog.append("Date: ${findViewById<TextView>(R.id.dateText).text}\n")
        workoutLog.append("Duration: $duration\n")
        workoutLog.append("Exercises:\n")

        // Create the workoutData1 JSON object for Section 1 (Workout History)
        val workoutData1 = JSONObject()
        workoutData1.put("WorkoutId", workoutId)  // Pass the workoutId
        workoutData1.put("WorkoutTime", duration) // Pass the workout duration

        // Log the workout data for debug purposes
        Log.d("WorkoutActivity", "Workout Data 1: $workoutData1")

        // Send Workout History to Backend (Section 1)
        sendWorkoutHistoryToBackend(workoutData1)
        // Send Workout Id to Section 5
        sendWorkoutIdToSection5(workoutId)

        val exercisesData = collectExerciseData()
        // Send the exercise data to Section 6
        sendExerciseDataToSection6(exercisesData)
    }

    private fun collectExerciseData(): JSONArray {
        val exercisesArray = JSONArray()

        // Assuming 'exercises' is a list of the exercises done in the workout
        for (exercise in exercises) {
            val exerciseJson = JSONObject()
            exerciseJson.put("ExerciseId", exercise.id)
            exerciseJson.put("ExerciseName", exercise.name)

            // Collect sets for this exercise
            val setsArray = JSONArray()
            val repsFrequency: MutableMap<Int, Int> = mutableMapOf()
            var maxWeight = 0.0
            var repsForMaxWeight = 0

            // First, iterate through the sets to calculate the most common reps and the max weight
            for (set in exercise.sets) {
                // Track the frequency of reps
                repsFrequency[set.reps] = repsFrequency.getOrDefault(set.reps, 0) + 1

                // Track the max weight used and the reps associated with it
                if (set.weight > maxWeight) {
                    maxWeight = set.weight
                    repsForMaxWeight = set.reps
                }
            }

            // Determine the most common reps, or if reps vary, use the reps for the max weight
            var mostCommonReps = repsForMaxWeight
            var highestFrequency = 0
            for ((reps, frequency) in repsFrequency) {
                if (frequency > highestFrequency) {
                    highestFrequency = frequency
                    mostCommonReps = reps
                }
            }

            // Now, create the final set data
            var totalSets = 0
            for ((index, set) in exercise.sets.withIndex()) {
                val setJson = JSONObject()
                setJson.put("SetNumber", index + 1)
                setJson.put("Reps", mostCommonReps)  // Use the determined reps
                setJson.put("Weight", set.weight)
                setsArray.put(setJson)
                totalSets++
            }

            // Add the sets array to the exercise JSON
            exerciseJson.put("Sets", setsArray)

            // Add the exercise data to the main array
            exercisesArray.put(exerciseJson)
        }

        return exercisesArray
    }


    // This method ensures set history is only sent after receiving historyId from section 1
    private fun sendWorkoutHistoryToBackend(workoutData: JSONObject) {
        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section1.php" // Adjust the URL

        // Make sure 'duration' is added to the workoutData JSON before sending
        val duration = workoutData.getString("WorkoutTime") // or get it from wherever you're setting it in workoutData

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, workoutData,
            Response.Listener { response ->
                // Handle the response from the server
                Log.d("WorkoutActivity", "Workout history saved: $response")

                // Check if the response contains the historyId
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        // Extract the historyId from the response
                        workoutHistoryId = response.getInt("historyId")
                        Log.d("WorkoutActivity", "Received historyId: $workoutHistoryId")

                        // Now that historyId is available, send set history data to backend
                        sendSetHistoryToBackend(workoutHistoryId!!, duration)  // Pass duration here
                    } else {
                        Log.e("WorkoutActivity", "Failed to save workout history: ${response.getString("message")}")
                    }
                } catch (e: Exception) {
                    Log.e("WorkoutActivity", "Error parsing response: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Handle any error
                Log.e("WorkoutActivity", "Error saving workout history: ${error.message}")
            }
        )

        // Create the request queue and add the request
        {}
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    fun calculateNewAverageSeconds(
        oldAvgTimeStr: String,
        workoutCountBefore: Int,
        newWorkoutSeconds: Int
    ): Int {
        val oldSeconds = convertTimeStringToSeconds(oldAvgTimeStr)
        val totalSeconds = (oldSeconds * workoutCountBefore + newWorkoutSeconds) / (workoutCountBefore + 1)
        return totalSeconds
    }

    fun convertTimeStringToSeconds(time: String): Int {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // HH:MM:SS
            2 -> parts[0] * 60 + parts[1]                  // MM:SS
            1 -> parts[0]                                   // SS
            else -> 0
        }
    }

    fun convertSecondsToTimeString(seconds: Int): String {
        val h = (seconds / 3600).toString().padStart(2, '0')
        val m = ((seconds % 3600) / 60).toString().padStart(2, '0')
        val s = (seconds % 60).toString().padStart(2, '0')
        return "$h:$m:$s"
    }

    private fun sendSetHistoryToBackend(workoutHistoryId: Int, duration: String) {
        val flatSetsArray = JSONArray()

        for (exercise in exercises) {
            for ((index, set) in exercise.sets.withIndex()) {
                val setJson = JSONObject().apply {
                    put("ExerciseId", exercise.id)
                    put("SetNumber", index + 1)
                    put("Reps", set.reps)
                    put("Weight", set.weight)
                }
                flatSetsArray.put(setJson)
            }
        }

        val setHistoryData = JSONObject().apply {
            put("historyId", workoutHistoryId)  // The workout history ID
            put("sets", flatSetsArray)           // The array of sets
        }

        // Log the data being sent
        Log.d("WorkoutActivity", "Sending data to backend: ${setHistoryData.toString()}")

        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section2.php"
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, setHistoryData,
            Response.Listener { response ->
                Log.d("WorkoutActivity", "✅ Set history saved: $response")
            },
            Response.ErrorListener { error ->
                Log.e("WorkoutActivity", "❌ Error saving set history: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)


    // ➕ Facts update logic (unchanged)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            var newSets = 0
            var newWeight = 0.0
            for (exercise in exercises) {
                for (set in exercise.sets) {
                    newSets++
                    newWeight += set.weight
                }
            }

            getFactsData { success, workouts, sets, weight, avgTime ->
                if (success) {
                    val updatedWorkouts = workouts + 1
                    val updatedSets = sets + newSets
                    val updatedWeight = weight + newWeight

                    // Convert current workout duration (String format like "MM:SS") to seconds
                    val newWorkoutSeconds = convertTimeStringToSeconds(duration)

                    // Calculate the new average time in seconds
                    val newAvgSeconds = calculateNewAverageSeconds(avgTime.toString(), workouts, newWorkoutSeconds)

                    // Convert back to "HH:MM:SS" format
                    val updatedAvgTime = convertSecondsToTimeString(newAvgSeconds)

                    sendFactsToSection4(userId, updatedWorkouts, updatedSets, updatedWeight, updatedAvgTime)
                }
            }
        }
    }



    private fun getFactsData(onResult: (Boolean, Int, Int, Int, String) -> Unit) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Log.e("PlansFragment", "❌ Invalid user ID")
            onResult(false, 0, 0, 0, "0.0")
            return
        }

        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section3.php"

        val jsonBody = JSONObject().apply {
            put("userId", userId)
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        val dataArray = response.getJSONArray("data")
                        val data = dataArray.getJSONObject(0)

                        val workoutsComplete = data.getInt("WorkoutsComplete")
                        val setsCompleted = data.getInt("SetsCompleted")
                        val totalWeight = data.getInt("TotalWeight")
                        val avgWorkoutTime = data.getString("AvgWorkoutTime")

                        Log.d("Section3", "✅ Workouts=$workoutsComplete Sets=$setsCompleted Weight=$totalWeight AvgTime=$avgWorkoutTime")

                        // Pass the data to be used in Section 4
                        onResult(true, workoutsComplete, setsCompleted, totalWeight, avgWorkoutTime)
                    } else {
                        Log.e("Section3", "❌ Failed to get facts: ${response.optString("error")}")
                        onResult(false, 0, 0, 0, "0.0")
                    }
                } catch (e: Exception) {
                    Log.e("Section3", "❌ JSON parse error: ${e.message}")
                    e.printStackTrace()
                    onResult(false, 0, 0, 0, "0.0")
                }
            },
            Response.ErrorListener { error ->
                Log.e("Section3", "❌ Network error: ${error.message}")
                error.printStackTrace()
                onResult(false, 0, 0, 0, "0.0")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    private fun sendFactsToSection4(
        userId: Int,
        workoutsComplete: Int,
        setsCompleted: Int,
        totalWeight: Double,
        avgWorkoutTime: String
    ) {
        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section4.php"

        val jsonBody = JSONObject().apply {
            put("userId", userId)
            put("WorkoutsComplete", workoutsComplete)
            put("SetsCompleted", setsCompleted)
            put("TotalWeight", totalWeight)
            put("AvgWorkoutTime", avgWorkoutTime)
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        Log.d("Section4", "✅ Successfully updated user facts in section 4")
                    } else {
                        Log.e("Section4", "❌ Failed to update facts: ${response.optString("error")}")
                    }
                } catch (e: Exception) {
                    Log.e("Section4", "❌ JSON parse error: ${e.message}")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("Section4", "❌ Network error: ${error.message}")
                error.printStackTrace()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    private fun sendWorkoutIdToSection5(workoutId: Int) {
        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section5.php"

        // Create the JSON object with the workoutId
        val jsonBody = JSONObject().apply {
            put("WorkoutId", workoutId)
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, jsonBody,
            Response.Listener { response ->
                try {
                    if (response.getBoolean("success")) {
                        Log.d("Section5", "✅ Successfully sent workoutId to section 5")
                    } else {
                        Log.e("Section5", "❌ Failed to send workoutId: ${response.optString("error")}")
                    }
                } catch (e: Exception) {
                    Log.e("Section5", "❌ JSON parse error: ${e.message}")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("Section5", "❌ Network error: ${error.message}")
                error.printStackTrace()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
    }

    private fun sendExerciseDataToSection6(exercisesData: JSONArray) {
        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/write/section6.php" // Adjust URL

        val requestData = JSONObject()
        requestData.put("WorkoutId", workoutId)  // Pass the workoutId
        requestData.put("Exercises", exercisesData) // Pass the exercise data

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, requestData,
            Response.Listener { response ->
                // Handle the response from the server
                Log.d("WorkoutActivity", "Exercise data saved: $response")
            },
            Response.ErrorListener { error ->
                // Handle any error
                Log.e("WorkoutActivity", "Error saving exercise data: ${error.message}")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return hashMapOf("Content-Type" to "application/json")
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(jsonObjectRequest)
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