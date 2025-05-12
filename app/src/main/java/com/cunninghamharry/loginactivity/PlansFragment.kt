package com.cunninghamharry.loginactivity

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    val exercises: List<Exercise>,
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


data class Plan(
    val id: Int,
    val title: String,
    val workouts: List<Workout>
)

data class Exercise(
    val name: String,
    val sets: MutableList<SetModel>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(SetModel) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
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
        val url = "https://hc920.brighton.domains/muscleMetric/php/workout/workout.php?user_id=4"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val workoutsArray = response.getJSONArray("workouts")
                    val parsedWorkouts = parsePlans(workoutsArray)
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

    fun parsePlans(workoutsArray: JSONArray): List<Workout> {
        val workouts = mutableListOf<Workout>()
        try {
            for (i in 0 until workoutsArray.length()) {
                val workoutObject = workoutsArray.getJSONObject(i) // Access the objects in the array

                val workout = Workout(
                    id = workoutObject.getInt("id"),
                    title = workoutObject.getString("Name"),
                    exercises = parseExercises(workoutObject),
                    isCompleted = workoutObject.optInt("Completed", 0) == 1
                )

                workouts.add(workout)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return workouts
    }

    fun parseExercises(workoutObject: JSONObject): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        try {
            val exercisesArray = workoutObject.optJSONArray("exercises") ?: JSONArray()
            for (k in 0 until exercisesArray.length()) {
                val exerciseObject = exercisesArray.getJSONObject(k)

                val exercise = Exercise(
                    name = exerciseObject.getString("Name"),
                    sets = parseSets(exerciseObject)
                )

                exercises.add(exercise)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return exercises
    }

    fun parseSets(exerciseObject: JSONObject): MutableList<SetModel> {
        val sets = mutableListOf<SetModel>()
        try {
            val setsArray = exerciseObject.optJSONArray("sets") ?: JSONArray()
            for (l in 0 until setsArray.length()) {
                val setObject = setsArray.getJSONObject(l)

                val set = SetModel(
                    weight = setObject.getDouble("weight"),
                    reps = setObject.getInt("reps")
                )

                sets.add(set)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return sets
    }


}
