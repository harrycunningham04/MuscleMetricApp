package com.cunninghamharry.loginactivity

import SetAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ExerciseAdapter(
    private val exercises: MutableList<Exercise>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>() // Tracks expanded exercises

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position] // Get the current exercise

        // Set exercise name and details
        holder.exerciseName.text = exercise.name
        holder.exerciseDetails.text = "${exercise.sets.size} sets"

        // Expand/collapse logic (keeps data when collapsing)
        val isExpanded = expandedPositions.contains(position)
        holder.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.cardView.setOnClickListener {
            if (isExpanded) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position) // Smooth UI update
        }

        // ✅ Set adapter with the correct sets
        holder.setAdapter.setSets(exercise.sets)

        // ✅ Pass delete function to SetAdapter
        holder.setAdapter.onDelete = { pos ->
            if (pos in exercise.sets.indices) {
                exercise.sets.removeAt(pos) // Remove from the actual list
                holder.exerciseDetails.text = "${exercise.sets.size} sets" // Update UI count
                notifyItemChanged(position)
                onUpdate()
            }
        }

        // ✅ Handle adding a new set
        holder.addSetButton.setOnClickListener {
            val newSet = SetModel(weight = 0.0, reps = 0)
            exercise.sets.add(newSet)
            holder.setAdapter.setSets(exercise.sets) // Refresh dataset
            holder.setAdapter.notifyItemInserted(exercise.sets.size - 1)

            holder.exerciseDetails.text = "${exercise.sets.size} sets" // Update UI count
            onUpdate()
        }
    }

    override fun getItemCount(): Int = exercises.size

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val exerciseName: TextView = view.findViewById(R.id.exerciseName)
        val exerciseDetails: TextView = view.findViewById(R.id.exerciseDetails)
        val cardView: MaterialCardView = view.findViewById(R.id.exerciseCard)
        val detailsContainer: View = view.findViewById(R.id.detailsContainer)
        val setRecyclerView: RecyclerView = view.findViewById(R.id.setRecyclerView)
        val addSetButton: Button = view.findViewById(R.id.addSetButton)

        val setAdapter = SetAdapter(mutableListOf()) {} // Initialize without delete logic
        init {
            setRecyclerView.layoutManager = LinearLayoutManager(view.context)
            setRecyclerView.adapter = setAdapter
        }
    }
}