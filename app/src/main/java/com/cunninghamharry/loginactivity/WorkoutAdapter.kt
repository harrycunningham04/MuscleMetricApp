package com.cunninghamharry.loginactivity

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private var workouts: List<Workout>, private val onClick: (Workout) -> Unit) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.workoutTitle)
        val description: TextView = view.findViewById(R.id.workoutDescription)
        val exerciseContainer: LinearLayout = view.findViewById(R.id.exerciseContainer)
        val button: Button = view.findViewById(R.id.workoutButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(view)
    }

    fun updateWorkouts(newWorkouts: List<Workout>) {
        Log.d("WorkoutAdapter", "Updating workouts: received ${newWorkouts.size} workouts")
        this.workouts = newWorkouts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        Log.d("WorkoutAdapter", "Binding workout at position $position: ${workout.title}")

        holder.title.text = workout.title

        val exerciseNames = workout.exercises.map { it.name }
        Log.d("WorkoutAdapter", "Workout '${workout.title}' has ${exerciseNames.size} exercises: $exerciseNames")
        holder.description.text = exerciseNames.joinToString(" • ")

        holder.exerciseContainer.removeAllViews()

        workout.exercises.forEachIndexed { index, exercise ->
            Log.d("WorkoutAdapter", "Exercise #$index: ${exercise.name}, sets: ${exercise.sets.size}")

            val row = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 8)
            }

            val exerciseName = TextView(holder.itemView.context).apply {
                text = exercise.name
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val totalSets = exercise.sets.size
            val reps = exercise.sets.firstOrNull()?.reps ?: 0
            val lastWeight = exercise.sets.lastOrNull()?.weight ?: 0.0

            Log.d("WorkoutAdapter", "Exercise '${exercise.name}': $totalSets sets × $reps reps, last weight = $lastWeight")

            val exerciseDetails = TextView(holder.itemView.context).apply {
                text = "$totalSets sets × $reps reps, Last weight: $lastWeight kg"
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            row.addView(exerciseName)
            row.addView(exerciseDetails)

            holder.exerciseContainer.addView(row)
        }

        // Handle workout completion state
        if (workout.isCompleted) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
            holder.button.text = "✅"
            holder.button.isEnabled = false
            holder.button.setOnClickListener(null)
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.default_background))
            holder.button.text = "Start Workout"
            holder.button.isEnabled = true
            holder.button.setOnClickListener {
                Log.d("WorkoutAdapter", "Workout '${workout.title}' clicked")
                onClick(workout)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("WorkoutAdapter", "Adapter item count: ${workouts.size}")
        return workouts.size
    }
}
