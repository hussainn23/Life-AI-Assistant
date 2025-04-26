package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.databinding.FragmentMoodBinding

class MoodFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var binding: FragmentMoodBinding


    override fun onCreate() {
        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {


                }

                override fun onFinish() {
                    initiateLayout()
                }
            }.start()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "initiateData: " + e.message)
        }
    }

    private fun initiateLayout() {
        settingUpBinding()
    }

    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = FragmentMoodBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        onClicks()

    }

    private fun onClicks() {
        binding.apply {
            fabAddTask.setOnClickListener {
                showMoodBottomSheet()
            }
        }
        displayPieChart()
    }

    private fun displayPieChart() {
        binding.apply {
            // Prepopulate sample data (Mood distribution for the month)
            val entries = listOf(
                PieEntry(35f, "Happy"),
                PieEntry(25f, "Calm"),
                PieEntry(15f, "Sad"),
                PieEntry(15f, "Angry"),
                PieEntry(10f, "Anxious")
            )

            val colors = listOf(
                Color.parseColor("#FFD700"), // Happy - Gold
                Color.parseColor("#87CEFA"), // Calm - Light Blue
                Color.parseColor("#FF7F7F"), // Sad - Light Red
                Color.parseColor("#FF4500"), // Angry - Orange Red
                Color.parseColor("#9370DB")  // Anxious - Medium Purple
            )

            val dataSet = PieDataSet(entries, "Monthly Moods").apply {
                setColors(colors)
                valueTextColor = Color.WHITE
                valueTextSize = 14f
                sliceSpace = 3f
                selectionShift = 8f
            }

            val data = PieData(dataSet)

            pieChart.data = data

// Customize the PieChart
            pieChart.apply {
                setUsePercentValues(true)
                description.isEnabled = false
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(12f)
                centerText = "Mood Tracker"
                setCenterTextSize(18f)
                setHoleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                animateY(1400, Easing.EaseInOutQuad)
                legend.isEnabled = true
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.textSize = 12f
            }

// Refresh
            pieChart.invalidate()

        }

    }

    private fun showMoodBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_mood, null)
        bottomSheetDialog.setContentView(view)

        // Get views
        val selectedMoodEmoji = view.findViewById<TextView>(R.id.selectedMoodEmoji)
        val selectedMoodText = view.findViewById<TextView>(R.id.selectedMoodText)
        val btnSubmit = view.findViewById<MaterialButton>(R.id.btnSubmit)

        // Moods Map (emoji -> text)
        val moodMap = mapOf(
            R.id.mood5 to Pair("😢", "Sad"),
            R.id.mood4 to Pair("🙁", "Unhappy"),
            R.id.mood3 to Pair("😐", "Neutral"),
            R.id.mood2 to Pair("🙂", "Happy"),
            R.id.mood1 to Pair("😃", "Very Happy")
        )

        // Setup mood selection
        for ((id, pair) in moodMap) {
            view.findViewById<LinearLayout>(id).setOnClickListener {
                selectedMoodEmoji.text = pair.first
                selectedMoodText.text = pair.second
            }
        }

        // Submit button click
        btnSubmit.setOnClickListener {
            // You can fetch selected mood here
            val selectedEmoji = selectedMoodEmoji.text.toString()
            val selectedText = selectedMoodText.text.toString()

            /// pass the selected text to api and get the suggestions and adaptive suggestions,





            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }
}
