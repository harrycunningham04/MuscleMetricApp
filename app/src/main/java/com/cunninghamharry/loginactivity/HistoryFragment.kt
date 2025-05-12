import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cunninghamharry.loginactivity.ExerciseHistory
import com.cunninghamharry.loginactivity.R
import com.cunninghamharry.loginactivity.WorkoutHistory
import com.cunninghamharry.loginactivity.WorkoutHistoryAdapter
import com.cunninghamharry.loginactivity.WorkoutSet

class HistoryFragment : Fragment() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var emptyMessage: TextView
    private lateinit var historyAdapter: WorkoutHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        emptyMessage = view.findViewById(R.id.emptyMessage)
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Mock Data
       val historyList = listOf(
            WorkoutHistory(
                date = "Wed, Mar 12",
                title = "Workout Plan 1",
                duration = 60,
                exercises = listOf(
                    ExerciseHistory("Bench Press", listOf(WorkoutSet(100, 8), WorkoutSet(100, 8), WorkoutSet(100, 8))),
                    ExerciseHistory("Pull-ups", listOf(WorkoutSet(5, 10), WorkoutSet(7, 10), WorkoutSet(7, 10))),
                    ExerciseHistory("Bicep Curls", listOf(WorkoutSet(18, 12), WorkoutSet(18, 12), WorkoutSet(18, 12))),
                    ExerciseHistory("Tricep Extension", listOf(WorkoutSet(30, 12), WorkoutSet(30, 12), WorkoutSet(30, 12)))
                )
            ),
            WorkoutHistory(
                date = "Tue, Mar 11",
                title = "Workout Plan 2",
                duration = 60,
                exercises = listOf(
                    ExerciseHistory("Deadlift", listOf(WorkoutSet(120, 5), WorkoutSet(120, 5), WorkoutSet(120, 5))),
                    ExerciseHistory("Lat Pulldown", listOf(WorkoutSet(50, 10), WorkoutSet(55, 10), WorkoutSet(55, 10))),
                    ExerciseHistory("Hammer Curls", listOf(WorkoutSet(20, 10), WorkoutSet(20, 10), WorkoutSet(20, 10)))
                )
            ),
            WorkoutHistory(
                date = "Mon, Mar 10",
                title = "Workout Plan 3",
                duration = 60,
                exercises = listOf(
                    ExerciseHistory("Squats", listOf(WorkoutSet(110, 8), WorkoutSet(110, 8), WorkoutSet(110, 8))),
                    ExerciseHistory("Leg Press", listOf(WorkoutSet(180, 12), WorkoutSet(190, 12), WorkoutSet(190, 12))),
                    ExerciseHistory("Calf Raises", listOf(WorkoutSet(40, 15), WorkoutSet(40, 15), WorkoutSet(40, 15)))
                )
            ),
            WorkoutHistory(
                date = "Sun, Mar 9",
                title = "Workout Plan 4",
                duration = 60,
                exercises = listOf(
                    ExerciseHistory("Overhead Press", listOf(WorkoutSet(50, 8), WorkoutSet(50, 8), WorkoutSet(50, 8))),
                    ExerciseHistory("Dips", listOf(WorkoutSet(10, 12), WorkoutSet(15, 12), WorkoutSet(15, 12))),
                    ExerciseHistory("Face Pulls", listOf(WorkoutSet(25, 15), WorkoutSet(25, 15), WorkoutSet(25, 15)))
                )
            ),
            WorkoutHistory(
                date = "Sat, Mar 8",
                title = "Workout Plan 5",
                duration = 60,
                exercises = listOf(
                    ExerciseHistory("Chest Fly", listOf(WorkoutSet(15, 12), WorkoutSet(15, 12), WorkoutSet(15, 12))),
                    ExerciseHistory("Lateral Raises", listOf(WorkoutSet(10, 12), WorkoutSet(12, 12), WorkoutSet(12, 12))),
                    ExerciseHistory("Rear Delt Fly", listOf(WorkoutSet(12, 12), WorkoutSet(12, 12), WorkoutSet(12, 12)))
                )
            )
        )

        historyAdapter = WorkoutHistoryAdapter(historyList)
        historyRecyclerView.adapter = historyAdapter

        if (historyList.isEmpty()) {
            emptyMessage.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
        } else {
            emptyMessage.visibility = View.GONE
            historyRecyclerView.visibility = View.VISIBLE

            historyAdapter = WorkoutHistoryAdapter(historyList)
            historyRecyclerView.adapter = historyAdapter
        }

        return view
    }
}
