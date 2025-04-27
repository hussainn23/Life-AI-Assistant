package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.adapters.MoodsAdapter
import com.softec.lifeaiassistant.adapters.OnMoodOptionClickListener // ✅ Correct Import
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.GeminiResponseFormatter
import com.softec.lifeaiassistant.databinding.FragmentMoodBinding
import com.softec.lifeaiassistant.geminiClasses.GetChatResponseText
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.utils.SharedPrefManager
import com.softec.lifeaiassistant.utils.Utils
import com.softec.lifeaiassistant.viewModel.MoodViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoodFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home), OnMoodOptionClickListener { // ✅ Correct Interface

    private lateinit var binding: FragmentMoodBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var viewModel: MoodViewModel
    private lateinit var utils: Utils
    private lateinit var moodsAdapter: MoodsAdapter
    private var moodsList = listOf<MoodModel>()

    override fun onCreate() {
        utils = Utils(context)
        viewModel = ViewModelProvider(context)[MoodViewModel::class.java]
        sharedPrefManager = SharedPrefManager(context)

        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {}
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
            alpha = 0f
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        setupRecyclerView()
        onClicks()
    }

    private fun setupRecyclerView() {
        moodsAdapter = MoodsAdapter(moodsList, this) // ✅ Implementing Correct Listener
        binding.moodRec.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodsAdapter
        }
        loadMoods()
    }

    private fun loadMoods() {
        viewModel.getMoodsList(sharedPrefManager.getUserId().orEmpty())
            .observe(context) { task ->
                task?.let {
                    moodsList = it
                    moodsAdapter.updateList(moodsList)
                    sharedPrefManager.saveMoods(moodsList)
                }
            }
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
            val entries = listOf(
                PieEntry(35f, "Happy"),
                PieEntry(25f, "Calm"),
                PieEntry(15f, "Sad"),
                PieEntry(15f, "Angry"),
                PieEntry(10f, "Anxious")
            )

            val colors = listOf(
                Color.parseColor("#FFD700"),
                Color.parseColor("#87CEFA"),
                Color.parseColor("#FF7F7F"),
                Color.parseColor("#FF4500"),
                Color.parseColor("#9370DB")
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
            pieChart.invalidate()
        }
    }

    private fun showMoodBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_mood, null)
        bottomSheetDialog.setContentView(view.rootView)

        val selectedMoodEmoji = view.findViewById<TextView>(R.id.selectedMoodEmoji)
        val selectedMoodText = view.findViewById<TextView>(R.id.selectedMoodText)
        val btnSubmit = view.findViewById<MaterialButton>(R.id.btnSubmit)

        val moodMap = mapOf(
            R.id.mood5 to Pair("😢", "Sad"),
            R.id.mood4 to Pair("🙁", "Unhappy"),
            R.id.mood3 to Pair("😐", "Neutral"),
            R.id.mood2 to Pair("🙂", "Happy"),
            R.id.mood1 to Pair("😃", "Very Happy")
        )

        for ((id, pair) in moodMap) {
            view.findViewById<LinearLayout>(id).setOnClickListener {
                selectedMoodEmoji.text = pair.first
                selectedMoodText.text = pair.second
            }
        }

        btnSubmit.setOnClickListener {
            val selectedEmoji = selectedMoodEmoji.text.toString()
            val selectedText = selectedMoodText.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val query = """
                    You are a helpful assistant. Based on the user's current mood, suggest appropriate activities or ideas.

                    Mood: $selectedText

                    1. Provide 5 general suggestions that are suitable for this mood.
                    2. Provide 5 adaptive suggestions that are better personalized to the mood's intensity or type.

                    Each suggestion should be short, specific, and categorized under "Suggestion" or "Adaptive Suggestion".
                    """

                    Log.d("MoodQuery", query)
                    utils.startLoadingAnimation()

                    val response = GetChatResponseText.getResponse(query)
                    Log.e("GeminiRawResponse", response)

                    if (response.isNotBlank()) {
                        val result = GeminiResponseFormatter.parseSuggestions(response)

                        val moodModel = MoodModel(
                            mood = selectedText,
                            suggesstions = result.suggestions.toString(),
                            adaptiveSuggestions = result.adaptiveSuggestions.toString(),
                            userId = sharedPrefManager.getUserId().orEmpty()
                        )

                        viewModel.saveMoodData(moodModel)
                        utils.endLoadingAnimation()

                        loadMoods()

                    } else {
                        utils.endLoadingAnimation()
                        Toast.makeText(context, "Empty Gemini response", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    utils.endLoadingAnimation()
                    e.printStackTrace()
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

    // ✅ Correct Interface Implementation
    override fun onOptionClicks(mood: MoodModel) {
        Toast.makeText(context, "Clicked: ${mood.mood}", Toast.LENGTH_SHORT).show()
    }
}
