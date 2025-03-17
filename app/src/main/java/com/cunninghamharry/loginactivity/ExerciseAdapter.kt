package com.cunninghamharry.loginactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ExerciseAdapter(    private val exercises: List<Exercise>,
                          private val onItemClick: (Exercise) -> Unit ) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.exerciseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        // Use string resource instead of text concatenation
        holder.exerciseName.text = exercise.name
        holder.exerciseDetails.text = holder.itemView.context.getString(
            R.string.exercise_details, exercise.sets, exercise.reps
        )

        // Expand/collapse logic
        val isExpanded = expandedPositions.contains(position)
        holder.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.cardView.setOnClickListener {
            if (isExpanded) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }

        // Ensure RecyclerView only initializes once
        if (holder.setRecyclerView.adapter == null) {
            holder.setRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.setRecyclerView.adapter = holder.setAdapter
        }

        // Add set button logic
        holder.addSetButton.setOnClickListener {
            holder.setList.add(SetModel(0, 8))  // Add new set
            holder.setAdapter.notifyDataSetChanged()  // ✅ Correctly notify adapter
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

        // Define the list first
        val setList: MutableList<SetModel> = mutableListOf(SetModel(0, 8), SetModel(0, 8), SetModel(0, 8))

        // Initialize adapter after defining setList
        val setAdapter: SetAdapter = SetAdapter(setList) { pos ->
            setList.removeAt(pos)
        }
    }
}
