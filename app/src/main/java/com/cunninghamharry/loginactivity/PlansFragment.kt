package com.cunninghamharry.loginactivity

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class Workout(
    val id: Int,
    val title: String,
    val exercises: List<Exercise> = emptyList(),
    val isCompleted: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Exercise) ?: emptyList(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeTypedList(exercises)
        parcel.writeByte(if (isCompleted) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Workout> {
        override fun createFromParcel(parcel: Parcel): Workout = Workout(parcel)
        override fun newArray(size: Int): Array<Workout?> = arrayOfNulls(size)
    }
}

data class Exercise(
    val id: Int = -1,
    val name: String,
    val sets: MutableList<SetModel>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createTypedArrayList(SetModel) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeTypedList(sets)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise = Exercise(parcel)
        override fun newArray(size: Int): Array<Exercise?> = arrayOfNulls(size)
    }
}

data class SetModel(
    var weight: Double,
    var reps: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(weight)
        parcel.writeInt(reps)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SetModel> {
        override fun createFromParcel(parcel: Parcel): SetModel = SetModel(parcel)
        override fun newArray(size: Int): Array<SetModel?> = arrayOfNulls(size)
    }
}

class PlansFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noWorkoutsText: TextView
    private lateinit var adapter: WorkoutAdapter
    private var workouts: MutableList<Workout> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plans, container, false)

        recyclerView = view.findViewById(R.id.workoutRecyclerView)
        noWorkoutsText = view.findViewById(R.id.noWorkoutsText)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = WorkoutAdapter(workouts) { workout ->
            val intent = Intent(context, WorkoutActivity::class.java).apply {
                putExtra("workout_id", workout.id)
                putExtra("workout_name", workout.title)
                putParcelableArrayListExtra("exercises", ArrayList(workout.exercises))
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        fetchPlans()

        return view
    }

    private fun fetchPlans() {
        // Get user_id from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        Log.d("PlansFragment", "Fetched user_id = $userId")

        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/workout.php?user_id=$userId"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val workoutsArray = response.getJSONArray("workouts")
                    val exercisesArray = response.getJSONArray("exercises")

                    val parsedExercises = parseAllExercises(exercisesArray)

                    // Group exercises by WorkoutId
                    val exercisesByWorkoutId = parsedExercises.groupBy { it.first }

                    val parsedWorkouts = parseWorkoutsWithExercises(workoutsArray, exercisesByWorkoutId)

                    workouts.clear()
                    workouts.addAll(parsedWorkouts)
                    adapter.notifyDataSetChanged()

                    noWorkoutsText.visibility = if (workouts.isEmpty()) View.VISIBLE else View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(request)
    }

    fun parseAllExercises(exercisesArray: JSONArray): List<Pair<Int, Exercise>> {
        val exercises = mutableListOf<Pair<Int, Exercise>>()
        for (i in 0 until exercisesArray.length()) {
            val exerciseObject = exercisesArray.getJSONObject(i)

            val workoutId = exerciseObject.getInt("WorkoutId")
            val exerciseId = exerciseObject.getInt("ExerciseId")  // Extract ExerciseId
            val setsCount = exerciseObject.getInt("Sets")  // Get the number of sets
            val reps = exerciseObject.optInt("Reps", 0)  // Get the reps value
            val weight = exerciseObject.optString("Weight", "0.0").toDoubleOrNull() ?: 0.0  // Get the weight value

            val sets = mutableListOf<SetModel>()
            // Add the same set for each repetition based on the "Sets" value
            for (i in 0 until setsCount) {
                sets.add(SetModel(weight = weight, reps = reps))
            }

            val exercise = Exercise(
                id = exerciseId,  // Set ExerciseId
                name = exerciseObject.getString("Name"),
                sets = sets  // Add the created sets list
            )

            exercises.add(Pair(workoutId, exercise))
        }
        return exercises
    }



    fun parseWorkoutsWithExercises(
        workoutsArray: JSONArray,
        exercisesByWorkoutId: Map<Int, List<Pair<Int, Exercise>>>
    ): List<Workout> {
        val workouts = mutableListOf<Workout>()
        for (i in 0 until workoutsArray.length()) {
            val workoutObject = workoutsArray.getJSONObject(i)

            val workoutId = workoutObject.getInt("id")
            val workoutExercises = exercisesByWorkoutId[workoutId]?.map { it.second } ?: emptyList()

            val workout = Workout(
                id = workoutId,
                title = workoutObject.getString("Name"),
                exercises = workoutExercises,
                isCompleted = workoutObject.optInt("Completed", 0) == 1
            )

            workouts.add(workout)
        }
        return workouts
    }
}
