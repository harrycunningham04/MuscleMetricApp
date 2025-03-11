package com.cunninghamharry.loginactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private val workouts: List<Workout>, private val onClick: (Workout) -> Unit) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.workoutTitle)
        val description: TextView = view.findViewById(R.id.workoutDescription)
        val exercises: TextView = view.findViewById(R.id.exerciseList)
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
        holder.exercises.text = workout.exercises.joinToString("\n") {
            "${it.name}  ${it.sets} sets × ${it.reps} reps"
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


