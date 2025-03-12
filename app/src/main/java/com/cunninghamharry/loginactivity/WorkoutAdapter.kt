package com.cunninghamharry.loginactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private val workouts: List<Workout>, private val onClick: (Workout) -> Unit) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.workoutTitle)
        val description: TextView = view.findViewById(R.id.workoutDescription)
        val exerciseContainer: LinearLayout = view.findViewById(R.id.exerciseContainer) // <-- Add this
        val button: Button = view.findViewById(R.id.workoutButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_card, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]

        holder.title.text = workout.title
        holder.description.text = workout.description

        // Clear previous views to prevent duplication
        holder.exerciseContainer.removeAllViews()

        workout.exercises.forEach { exercise ->
            val row = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val exerciseName = TextView(holder.itemView.context).apply {
                text = exercise.name
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val exerciseDetails = TextView(holder.itemView.context).apply {
                text = "${exercise.sets} sets × ${exercise.reps} reps"
                textSize = 14f
                textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            row.addView(exerciseName)
            row.addView(exerciseDetails)

            holder.exerciseContainer.addView(row)
        }

        if (workout.isCompleted) {
            holder.button.text = "✅"
            holder.button.isEnabled = false // Disable button
        } else {
            holder.button.text = "Start Workout"
            holder.button.isEnabled = true
            holder.button.setOnClickListener { onClick(workout) }
        }
    }

    override fun getItemCount() = workouts.size
}


