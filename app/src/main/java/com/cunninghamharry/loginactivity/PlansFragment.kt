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

data class Workout(
    val id: Int,
    val title: String,
    val description: String,
    val exercises: List<Exercise>,
    val isCompleted: Boolean
)

data class Exercise(val name: String, val sets: Int, val reps: Int, val weight: Double) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(sets)
        parcel.writeInt(reps)
        parcel.writeDouble(weight)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise = Exercise(parcel)
        override fun newArray(size: Int): Array<Exercise?> = arrayOfNulls(size)
    }
}


class PlansFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var noWorkoutsText: TextView
    private lateinit var adapter: WorkoutAdapter
    private var workouts: List<Workout> = listOf() // Replace with actual workout data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plans, container, false)

        recyclerView = view.findViewById(R.id.workoutRecyclerView)
        noWorkoutsText = view.findViewById(R.id.noWorkoutsText)

        // Fake Workout Data
        workouts = listOf(
            Workout(
                id = 1,
                title = "Upper Body Focus",
                description = "Chest, back, and arms workout",
                exercises = listOf(
                    Exercise("Bench Press", 4, 6, 8.0),
                    Exercise("Pull-ups", 3, 5, 10.0),
                    Exercise("Bicep Curls", 3, 12, 12.0)
                ),
                isCompleted = true
            ),
            Workout(
                id = 2,
                title = "Leg Day",
                description = "Legs and glutes workout",
                exercises = listOf(
                    Exercise("Squats", 4, 8, 10.0),
                    Exercise("Lunges", 3, 12, 12.0),
                    Exercise("Calf Raises", 3, 20, 15.0)
                ),
                isCompleted = false
            ),
            Workout(
                id = 3,
                title = "Full Body Strength",
                description = "Full-body compound exercises",
                exercises = listOf(
                    Exercise("Deadlifts", 4, 8, 6.0),
                    Exercise("Overhead Press", 3, 6, 8.0),
                    Exercise("Pull-ups", 3, 5, 10.0)
                ),
                isCompleted = false
            )
        )

        // Check if there are workouts
        if (workouts.isEmpty()) {
            noWorkoutsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noWorkoutsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = WorkoutAdapter(workouts) { workout ->
                val intent = Intent(context, WorkoutActivity::class.java).apply {
                    putExtra("workout_name", workout.title)
                    putParcelableArrayListExtra("exercises", ArrayList(workout.exercises))
                }
                context?.startActivity(intent)
            }
        }

        return view
    }
}
