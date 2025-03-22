import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cunninghamharry.loginactivity.R
import com.cunninghamharry.loginactivity.SetModel

class SetAdapter(
    private val sets: MutableList<SetModel>,
    var onDelete: (Int) -> Unit
) : RecyclerView.Adapter<SetAdapter.SetViewHolder>() {

    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val setNumber: TextView = view.findViewById(R.id.setNumber)
        val weightInput: EditText = view.findViewById(R.id.weightInput)
        val repsInput: EditText = view.findViewById(R.id.repsInput)
        val deleteButton: ImageView = view.findViewById(R.id.deleteSetButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set_row, parent, false)
        return SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = (position + 1).toString()  // Update set number
        holder.weightInput.setText(set.weight.toString())
        holder.repsInput.setText(set.reps.toString())

        // Handle delete set
        holder.deleteButton.setOnClickListener {
            removeSet(position)
        }
    }

    override fun getItemCount() = sets.size

    fun removeSet(position: Int) {
        if (position in sets.indices) {
            sets.removeAt(position)  // Remove the set
            notifyItemRemoved(position)  // Notify only the removed item
            notifyItemRangeChanged(position, sets.size)  // Update remaining items' numbers

            onDelete(position)  // 🔥 Notify the parent adapter (ExerciseAdapter) that something changed
        }
    }

    fun setSets(newSets: List<SetModel>) {
        sets.clear()
        sets.addAll(newSets)
        notifyDataSetChanged()
    }
}