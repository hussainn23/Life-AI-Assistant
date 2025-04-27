package com.softec.lifeaiassistant.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.models.TaskModel


class ReminderAdapter(private val reminders: List<TaskModel>) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {



    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val heading: TextView = itemView.findViewById(R.id.title)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val icon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(reminder: TaskModel) {
            if (reminder.reminder=="true") {

                Log.e("l", "bind: called")
                heading.text = reminder.taskName
                time.text = reminder.subCategory
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shedule, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int = reminders.size
}
