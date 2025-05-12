package com.cunninghamharry.loginactivity

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexWrap

data class WorkoutHistory(
    val date: String,
    val title: String,
    val exercises: List<ExerciseHistory>,
    val duration: String
)

data class ExerciseHistory(
    val name: String,
    val sets: Int
)

data class WorkoutSet(
    val weight: Int,
    val reps: Int
)

class WorkoutHistoryAdapter(private val workouts: List<WorkoutHistory>) :
    RecyclerView.Adapter<WorkoutHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutDate: TextView = view.findViewById(R.id.workoutDate)
        val workoutTitle: TextView = view.findViewById(R.id.workoutTitle)
        val workoutDuration: TextView = itemView.findViewById(R.id.workoutDuration)
        val exerciseContainer: LinearLayout = view.findViewById(R.id.exerciseContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val workout = workouts[position]
        holder.workoutDate.text = workout.date
        holder.workoutTitle.text = workout.title
        holder.workoutDuration.text = "${workout.duration} min"

        // Clear previous views
        holder.exerciseContainer.removeAllViews()

        // Group exercises by name and count occurrences
        val exerciseMap = workout.exercises.groupBy { it.name }

        // Add exercises dynamically
        exerciseMap.forEach { (name, exercises) ->
            // Create a new LinearLayout to hold the exercise name and sets
            val exerciseLayout = LinearLayout(holder.itemView.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // Add exercise title on the left
            val exerciseTitle = TextView(holder.itemView.context).apply {
                text = name
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 8, 0, 4)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) // Take up available space
            }
            exerciseLayout.addView(exerciseTitle)

            // Add sets on the right
            exercises.forEach { exerciseHistory ->
                val setTextView = TextView(holder.itemView.context).apply {
                    text = "${exerciseHistory.sets} sets"
                    textSize = 14f
                    setBackgroundResource(R.drawable.set_background)
                    setPadding(16, 8, 16, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                exerciseLayout.addView(setTextView)
            }

            // Add the exercise layout to the container
            holder.exerciseContainer.addView(exerciseLayout)
        }
    }

    override fun getItemCount(): Int = workouts.size
}


