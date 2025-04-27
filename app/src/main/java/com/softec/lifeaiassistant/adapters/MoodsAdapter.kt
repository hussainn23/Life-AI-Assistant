package com.softec.lifeaiassistant.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.models.MoodModel

// Correct Interface
interface OnMoodOptionClickListener {
    fun onOptionClicks(mood: MoodModel)
}

class MoodsAdapter(
    private var moodsList: List<MoodModel>,
    private val optionClickListener: OnMoodOptionClickListener
) : RecyclerView.Adapter<MoodsAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodName: TextView = itemView.findViewById(R.id.serName)
        val moodSuggestions: TextView = itemView.findViewById(R.id.suggestion)
        val optionsIcon: ShapeableImageView = itemView.findViewById(R.id.ivMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodsList[position]
        holder.moodName.text = mood.mood
        holder.moodSuggestions.text = mood.suggesstions

        holder.optionsIcon.setOnClickListener {
            optionClickListener.onOptionClicks(mood)
        }
    }

    override fun getItemCount(): Int {
        return moodsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newMoodsList: List<MoodModel>) {
        moodsList = newMoodsList
        notifyDataSetChanged()
    }
}
