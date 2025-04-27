package com.softec.lifeaiassistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.models.MoodModel

interface OnMoodOptionClickListener {
    fun onOptionClicks(mood: MoodModel, view: View)
}

class MoodsAdapter(
    private var moodsList: List<MoodModel>,
    private val optionClickListener: OnMoodOptionClickListener
) : RecyclerView.Adapter<MoodsAdapter.MoodViewHolder>() {

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moodIcon: TextView = itemView.findViewById(R.id.img)
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
        holder.moodIcon.text = getMoodEmoji(mood.mood)
        holder.optionsIcon.setOnClickListener {
            optionClickListener.onOptionClicks(mood, it)
        }
        holder.itemView.setOnClickListener {
            showMoodDetails(holder.itemView.context, mood)
        }
    }


    @SuppressLint("CutPasteId")
    fun showMoodDetails(context: Context, moodModel: MoodModel) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_show_mood_detial, null)

        view.findViewById<TextView>(R.id.img).text = getEmojiForMood(moodModel.mood)
        view.findViewById<TextView>(R.id.serName).text = moodModel.mood
        view.findViewById<TextView>(R.id.suggestion).text = moodModel.suggesstions
        view.findViewById<TextView>(R.id.adaptivesuggestion).text = moodModel.adaptiveSuggestions

        if (moodModel.adaptiveSuggestions.isEmpty()) {
            view.findViewById<TextView>(R.id.tvadapsuggestion).visibility = View.GONE
            view.findViewById<TextView>(R.id.adaptivesuggestion).visibility = View.GONE
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        bottomSheetDialog.behavior.peekHeight =
            (Resources.getSystem().displayMetrics.heightPixels * 0.6).toInt()
    }


    private fun getEmojiForMood(mood: String): String {
        return when (mood.lowercase()) {
            "happy" -> "😊"
            "sad" -> "😢"
            "angry" -> "😠"
            "anxious" -> "😰"
            "excited" -> "🤩"
            "tired" -> "😴"
            else -> "😐"
        }
    }

    fun getMoodEmoji(mood: String): String {
        return if (mood.equals("Very Happy", ignoreCase = true)) {
            "😃"
        } else if (mood.equals("Happy", ignoreCase = true)) {
            "🙂"
        } else if (mood.equals("Neutral", ignoreCase = true)) {
            "😐"
        } else if (mood.equals("Unhappy", ignoreCase = true)) {
            "🙁"
        } else if (mood.equals("Sad", ignoreCase = true)) {
            "😢"
        } else {
            "😐" // Default if no match
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
