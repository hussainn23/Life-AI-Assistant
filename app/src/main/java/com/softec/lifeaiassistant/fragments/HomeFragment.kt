package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.SignupViewModelFactory
import com.softec.lifeaiassistant.databinding.HomeFragmentBinding
import com.softec.lifeaiassistant.databinding.LayoutFragmentHomeBinding
import com.softec.lifeaiassistant.models.ModelUser
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.utils.SharedPrefManager
import com.softec.lifeaiassistant.viewModel.LoginViewModel
import com.softec.lifeaiassistant.viewModel.MoodViewModel
import com.softec.lifeaiassistant.viewModel.SignupViewModel
import com.softec.lifeaiassistant.viewModel.TaskViewModel
import java.util.Map

class HomeFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {
//        home_fragment.xml

    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private var userId:String?=null
    private lateinit var viewModel: TaskViewModel
    private lateinit var moodViewModel: MoodViewModel
    private var tasksList = listOf<TaskModel>()
    private var moodsList = listOf<MoodModel>()






    override fun onCreate() {
        sharedPrefManager = SharedPrefManager(context)
        getUser(sharedPrefManager.getUserId())
        userId=sharedPrefManager.getUserId()

        viewModel = ViewModelProvider(context)[TaskViewModel::class.java]
        moodViewModel = ViewModelProvider(context)[MoodViewModel::class.java]
        fetchTasks()
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





    private fun fetchTasks(){
        viewModel.getTasksList(userId).observe(context) { task ->
            task?.let {
                tasksList = it
                sharedPrefManager.saveTasks(tasksList)
            }
        }


        moodViewModel.getMoodsList(sharedPrefManager.getUserId()).observe(context) { task ->
            task?.let {
                moodsList = it
                sharedPrefManager.saveMoods(moodsList)
            }
        }


    }












    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = HomeFragmentBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        setupLineChart()

    }


    private fun getUser(userId: String?) {
        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)
        Toast.makeText(context, "debug ; "+userId, Toast.LENGTH_SHORT).show()


        db.collection("Users") // Your collection name
            .whereEqualTo("userId", id) // Comparing the userId field
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents.firstOrNull()
                    if (document != null) {
                        val user = document.toObject(ModelUser::class.java)
                        if (user != null) {
                            sharedPrefManager.saveUser(user)
                            Toast.makeText(context, "User: ${user.userName}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to parse user", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun setupLineChart() {
        binding.apply {

        // Fixed data matching the reference image style
        val yearLabels = arrayOf("1st Week", "2012", "2013", "2014", "2015", "2016", "2017", "2018")
        val dataValues = listOf(45f, 60f, 75f, 90f, 110f, 130f, 150f, 180f) // Sample market process data

        // 1. Prepare chart entries
        val entries = mutableListOf<Entry>()
        dataValues.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value))
        }

        // 2. Create dataset with reference image styling
        val dataSet = LineDataSet(entries, "Markets Process").apply {
            color = ContextCompat.getColor(context, R.color.chart_line) // Dark line color
            valueTextColor = ContextCompat.getColor(context, R.color.text_primary)
            lineWidth = 2.5f
            setCircleColor(ContextCompat.getColor(context, R.color.chart_line))
            circleRadius = 4f
            circleHoleRadius = 2f
            setDrawCircleHole(true)
            valueTextSize = 10f
            mode = LineDataSet.Mode.LINEAR // Straight lines between points
            setDrawFilled(false) // No fill under line
            setDrawValues(false) // Don't show values above points
            setDrawHorizontalHighlightIndicator(false)
            setDrawVerticalHighlightIndicator(false)
        }

        // 3. Configure chart appearance to match reference
        with(lineChart) {
            data = LineData(dataSet)

            // X-axis configuration (years at bottom)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(yearLabels)
                granularity = 1f
                setDrawGridLines(false)
                labelCount = yearLabels.size
                axisLineColor = ContextCompat.getColor(context, R.color.axis_line)
                textColor = ContextCompat.getColor(context, R.color.text_primary)
                textSize = 11f
                setCenterAxisLabels(false)
                yOffset = 8f // Slight padding
            }

            // Y-axis configuration (left only)
            axisLeft.apply {
                granularity = 20f // Major steps every 20 units
                axisMinimum = 0f
                axisMaximum = 200f // Fixed maximum to match reference style
                axisLineColor = ContextCompat.getColor(context, R.color.axis_line)
                textColor = ContextCompat.getColor(context, R.color.text_primary)
                textSize = 11f
                setDrawZeroLine(false)
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.grid_line)
                gridLineWidth = 0.5f
                setDrawAxisLine(false) // No left axis line
            }

            // Additional chart settings
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false // No legend
            setTouchEnabled(true)
            setPinchZoom(false)

            // Remove all extra elements
            setDrawBorders(false)
            setBorderWidth(0f)
            setNoDataText("No data available")
            setNoDataTextColor(ContextCompat.getColor(context, R.color.text_secondary))

            // Background styling
            setBackgroundColor(Color.TRANSPARENT)
            setDrawGridBackground(false)

            // Remove right and top borders
            xAxis.setDrawAxisLine(true)
            axisLeft.setDrawAxisLine(false)

            // Add padding
            extraLeftOffset = 5f
            extraRightOffset = 15f
            extraBottomOffset = 5f

            // Simple animation
            animateY(800, Easing.Linear)

            invalidate()
        }
    }

    }
}
