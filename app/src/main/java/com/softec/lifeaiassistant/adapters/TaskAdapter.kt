package com.softec.lifeaiassistant.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.models.TaskModel
import com.google.android.material.imageview.ShapeableImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

// Interface for handling option clicks
interface OnOptionClickListener {
    fun onOptionClick(task: TaskModel)
}

class TaskAdapter(
    private var taskList: List<TaskModel>,
    private val optionClickListener: OnOptionClickListener // Pass the listener from the fragment/activity
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val serName: TextView = itemView.findViewById(R.id.serName)
        val subCategory: TextView = itemView.findViewById(R.id.duration)
        val options: ShapeableImageView = itemView.findViewById(R.id.options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Bind the data to the views
        holder.serName.text = task.taskName
        holder.subCategory.text = task.subCategory

        // Handle click event on the `options` icon
        holder.options.setOnClickListener {
            optionClickListener.onOptionClick(task) // Pass the clicked task to the fragment
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(taskList : List<TaskModel>){
        this.taskList = taskList
        notifyDataSetChanged()
    }
}
