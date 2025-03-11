package com.cunninghamharry.loginactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ExerciseAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.exerciseDetails.text = "${exercise.sets} sets • ${exercise.reps} reps"

        // Toggle expand/collapse
        holder.cardView.setOnClickListener {
            holder.detailsContainer.visibility =
                if (holder.detailsContainer.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
        }
    }

    override fun getItemCount(): Int = exercises.size

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val exerciseDetails: TextView = view.findViewById(R.id.exerciseDetails)
        val cardView: MaterialCardView = view.findViewById(R.id.exerciseCard)
        val detailsContainer: View = view.findViewById(R.id.detailsContainer)
    }
}
