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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Exercise) ?: emptyList(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
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

        // Sample Workouts
        workouts = mutableListOf(
            Workout(
                id = 1,
                title = "Upper Body Focus",
                description = "Chest, back, and arms workout",
                exercises = listOf(
                    Exercise("Bench Press", mutableListOf(SetModel(8.0, 6), SetModel(8.5, 6))),
                    Exercise("Pull-ups", mutableListOf(SetModel(10.0, 5), SetModel(10.5, 5))),
                    Exercise("Bicep Curls", mutableListOf(SetModel(12.0, 12), SetModel(12.5, 12)))
                ),
                isCompleted = true
            ),
            Workout(
                id = 2,
                title = "Leg Day",
                description = "Legs and glutes workout",
                exercises = listOf(
                    Exercise("Squats", mutableListOf(SetModel(10.0, 8), SetModel(10.5, 8))),
                    Exercise("Lunges", mutableListOf(SetModel(12.0, 12), SetModel(12.5, 12))),
                    Exercise("Calf Raises", mutableListOf(SetModel(15.0, 20), SetModel(16.0, 20)))
                ),
                isCompleted = false
            )
        )

        // Check if workouts exist
        if (workouts.isEmpty()) {
            noWorkoutsText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            noWorkoutsText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            adapter = WorkoutAdapter(workouts) { workout ->
                val intent = Intent(context, WorkoutActivity::class.java).apply {
                    putExtra("workout_name", workout.title)
                    putParcelableArrayListExtra("exercises", ArrayList(workout.exercises))
                }
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }

        return view
    }
}
