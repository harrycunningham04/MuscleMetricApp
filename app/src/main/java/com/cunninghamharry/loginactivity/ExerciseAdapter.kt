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
        val exercise = exercises[position]

        // Set exercise name and details
        holder.exerciseName.text = exercise.name
        holder.exerciseDetails.text = "${exercise.sets.size} sets"

        // Expand/collapse logic
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

        // Update SetAdapter with current exercise sets
        holder.setAdapter.setSets(exercise.sets)

        // Handle adding a new set
        holder.addSetButton.setOnClickListener {
            exercise.sets.add(SetModel(weight = 0.0, reps = 10)) // Default reps = 10
            holder.setAdapter.notifyItemInserted(exercise.sets.size - 1) // Efficient update
            notifyItemChanged(position) // Update main UI
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

        val setAdapter: SetAdapter

        init {
            setRecyclerView.layoutManager = LinearLayoutManager(view.context)
            setAdapter = SetAdapter(mutableListOf()) { pos ->
                setAdapter.removeSet(pos)
            }
            setRecyclerView.adapter = setAdapter
        }
    }
}