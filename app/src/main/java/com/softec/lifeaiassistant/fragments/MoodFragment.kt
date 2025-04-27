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
import android.widget.PopupMenu
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
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.adapters.MoodsAdapter
import com.softec.lifeaiassistant.adapters.OnMoodOptionClickListener // ✅ Correct Import
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.GeminiResponseFormatter
import com.softec.lifeaiassistant.databinding.FragmentMoodBinding
import com.softec.lifeaiassistant.geminiClasses.GetChatResponseText
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.utils.Constants
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
            moodsList = sharedPrefManager.getMoods()!!
            // Get mood counts from the list
            Log.e("TAG", "displayPieChart: $moodsList" )

            val moodCounts = moodsList
                .filter { it.mood.isNotEmpty() } // Filter out empty/null moods
                .groupingBy { it.mood }
                .eachCount()

            Log.e("TAG", "displayPieChart: $moodCounts" )

            // Create pie chart entries
            val entries = moodCounts.map { (mood, count) ->
                PieEntry(count.toFloat(), mood)
            }

            Log.e("TAG", "displayPieChart: $entries" )


            // Use a predefined color palette that can handle dynamic number of moods
            val colors = listOf(
                Color.parseColor("#FFD700"), // Gold - Happy
                Color.parseColor("#87CEFA"), // Light Sky Blue - Calm
                Color.parseColor("#FF7F7F"), // Light Coral - Sad
                Color.parseColor("#FF4500"), // Orange Red - Angry
                Color.parseColor("#9370DB"), // Medium Purple - Anxious
                Color.parseColor("#3CB371"), // Medium Sea Green - Excited
                Color.parseColor("#FFA500"), // Orange - Tired
                Color.parseColor("#20B2AA")  // Light Sea Green - Relaxed
                // Add more colors if you expect more mood types
            )

            val dataSet = PieDataSet(entries, "Monthly Moods").apply {
                // Assign colors cyclically if we have more moods than colors

                colors.forEach {
                    addColor(it)
                }
                valueTextColor = Color.WHITE
                valueTextSize = 14f
                sliceSpace = 3f
                selectionShift = 8f
                valueFormatter = PercentFormatter(pieChart) // For percentage display
            }

            val data = PieData(dataSet)
            pieChart.data = data

            pieChart.apply {
                setUsePercentValues(true)
                description.isEnabled = false
                setEntryLabelColor(context.getColor(R.color.textColorSecondary))
                setEntryLabelTextSize(12f)
                centerText = "Mood Tracker"
                setCenterTextSize(18f)
                setHoleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                animateY(1400, Easing.EaseInOutQuad)

                // Legend configuration
                legend.isEnabled = true
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.textSize = 12f
                legend.textColor = context.getColor(R.color.textColorSecondary)

                // Additional styling
                isDrawHoleEnabled = true
                holeRadius = 40f
                transparentCircleRadius = 45f
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

Respond strictly in the following format:

**Suggestion:**
* Suggestion 1
* Suggestion 2
* Suggestion 3
* Suggestion 4
* Suggestion 5

**Adaptive Suggestion:**
* Adaptive Suggestion 1
* Adaptive Suggestion 2
* Adaptive Suggestion 3
* Adaptive Suggestion 4
* Adaptive Suggestion 5

Notes:
1. Provide exactly 5 general suggestions under "Suggestion" section
2. Provide exactly 5 adaptive suggestions under "Adaptive Suggestion" section
3. Each suggestion must start with "* " (asterisk and space)
4. Do not include any additional text, explanations, or headers beyond what's specified
5. Keep each suggestion short and specific
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

                        displayPieChart()


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



    override fun onOptionClicks(mood: MoodModel, view: View) {
        showPopupMenu(mood,view)
    }
    private fun showPopupMenu(mood: MoodModel, view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_mood, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    FirebaseFirestore.getInstance().collection(Constants.MOOD).document(mood.moodId)
                        .delete().addOnCompleteListener({task->
                            if(task.isSuccessful){
                                val moodViewModel = ViewModelProvider(context)[MoodViewModel::class.java]
                                moodViewModel.getMoodsList(mood.userId).observe(context) { task ->
                                    task?.let {
                                        moodsList = it
                                        sharedPrefManager.saveMoods(moodsList)
                                    }
                                }
                                moodsAdapter.updateList(moodsList.sortedByDescending { it.createdAt })

                                displayPieChart()
                                Toast.makeText(context, "Task deleted successfully.", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

}
