import android.text.Editable
import android.text.TextWatcher
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
        holder.setNumber.text = "Set ${position + 1}" // Show correct set number

        // ✅ Ensure the EditText fields persist data
        holder.weightInput.setText(set.weight.toString())
        holder.repsInput.setText(set.reps.toString())

        // ✅ Save user changes in real-time
        holder.weightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                set.weight = s.toString().toDoubleOrNull() ?: 0.0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.repsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                set.reps = s.toString().toIntOrNull() ?: 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle delete set
        holder.deleteButton.setOnClickListener {
            removeSet(position)
        }
    }

    override fun getItemCount() = sets.size

    fun removeSet(position: Int) {
        if (position in sets.indices) {
            sets.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, sets.size)

            onDelete(position) // Notify ExerciseAdapter that a set was deleted
        }
    }

    fun setSets(newSets: MutableList<SetModel>) {
        sets.clear()
        sets.addAll(newSets)
        notifyDataSetChanged()
    }
}