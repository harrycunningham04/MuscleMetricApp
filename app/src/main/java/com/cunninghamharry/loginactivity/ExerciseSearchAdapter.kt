package com.cunninghamharry.loginactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExerciseSearchAdapter(
    private val exercises: List<ExerciseNew>,
    private val onItemClick: (ExerciseNew) -> Unit
) : RecyclerView.Adapter<ExerciseSearchAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameText: TextView = view.findViewById(R.id.exerciseName)
        private val bodyPartText: TextView = view.findViewById(R.id.bodyPart)
        private val equipmentText: TextView = view.findViewById(R.id.equipment)

        fun bind(exercise: ExerciseNew) {
            nameText.text = exercise.name
            bodyPartText.text = "Target: ${exercise.bodyPart}"
            equipmentText.text = "Equipment: ${exercise.equipment}"

            itemView.setOnClickListener { onItemClick(exercise) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_search, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}