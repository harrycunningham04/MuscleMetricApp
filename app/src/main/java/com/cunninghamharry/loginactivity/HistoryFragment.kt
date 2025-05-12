import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.cunninghamharry.loginactivity.ExerciseHistory
import com.cunninghamharry.loginactivity.R
import com.cunninghamharry.loginactivity.WorkoutHistory
import com.cunninghamharry.loginactivity.WorkoutHistoryAdapter
import org.json.JSONArray

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

        fetchWorkoutHistory()

        return view
    }

    private fun fetchWorkoutHistory() {
        val url = "https://hc920.brighton.domains/muscleMetric/php/history/history.php?user_id=4"

        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                val historyList = parseWorkoutHistory(response)
                if (historyList.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                    historyRecyclerView.visibility = View.GONE
                } else {
                    emptyMessage.visibility = View.GONE
                    historyRecyclerView.visibility = View.VISIBLE
                    historyAdapter = WorkoutHistoryAdapter(historyList)
                    historyRecyclerView.adapter = historyAdapter
                }
            },
            { error ->
                emptyMessage.text = "Failed to load data."
                emptyMessage.visibility = View.VISIBLE
                historyRecyclerView.visibility = View.GONE
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    private fun parseWorkoutHistory(jsonArray: JSONArray): List<WorkoutHistory> {
        val historyList = mutableListOf<WorkoutHistory>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val date = obj.getString("date")
            val workoutName = obj.getString("workoutName") // Updated to match JSON field
            val duration = obj.getString("duration") // Duration is a string, so treat it as such

            val exercisesJson = obj.getJSONArray("exercises")
            val exerciseCountMap = mutableMapOf<String, Int>()

            // Count the occurrences of each exercise name
            for (j in 0 until exercisesJson.length()) {
                val exerciseName = exercisesJson.getString(j)
                exerciseCountMap[exerciseName] = exerciseCountMap.getOrDefault(exerciseName, 0) + 1
            }

            // Convert the map into a list of ExerciseHistory objects
            val exercises = exerciseCountMap.map { (name, count) ->
                ExerciseHistory(name, count)
            }

            // Add the parsed workout data to the history list
            historyList.add(WorkoutHistory(date, workoutName, exercises, duration))
        }

        // Sort the history list by date (most recent first)
        return historyList.sortedByDescending { it.date }
    }


}
