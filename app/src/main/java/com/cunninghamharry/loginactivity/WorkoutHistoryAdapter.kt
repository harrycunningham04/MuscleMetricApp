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
    val duration: Int
)

data class ExerciseHistory(
    val name: String,
    val sets: List<WorkoutSet>
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

        // Add exercises dynamically
        workout.exercises.forEach { exercise ->
            val exerciseTitle = TextView(holder.itemView.context).apply {
                text = exercise.name
                textSize = 16f
                setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 8, 0, 4)
            }
            holder.exerciseContainer.addView(exerciseTitle)

            val flexboxLayout = FlexboxLayout(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                flexWrap = FlexWrap.WRAP
            }

            exercise.sets.forEach { set ->
                val setTextView = TextView(holder.itemView.context).apply {
                    text = "${set.weight}kg × ${set.reps}"
                    textSize = 14f
                    setBackgroundResource(R.drawable.set_background)
                    setPadding(16, 8, 16, 8)
                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                }
                flexboxLayout.addView(setTextView)
            }

            holder.exerciseContainer.addView(flexboxLayout)
        }
    }

    override fun getItemCount(): Int = workouts.size
}

