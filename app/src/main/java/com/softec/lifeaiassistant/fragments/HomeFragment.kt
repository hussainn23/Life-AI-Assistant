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
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.ActivityNavigator
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.MainActivityInterface
import com.softec.lifeaiassistant.databinding.HomeFragmentBinding
import com.softec.lifeaiassistant.models.ModelUser
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.ui.ActivityProfile
import com.softec.lifeaiassistant.utils.SharedPrefManager
import com.softec.lifeaiassistant.viewModel.MoodViewModel
import com.softec.lifeaiassistant.viewModel.TaskViewModel
import java.util.Calendar

class HomeFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {
    //        home_fragment.xml
    private lateinit var intrface: MainActivityInterface
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private var userId: String? = null
    private lateinit var viewModel: TaskViewModel
    private lateinit var moodViewModel: MoodViewModel
    private var tasksList = listOf<TaskModel>()
    private var moodsList = listOf<MoodModel>()


    override fun onCreate() {
        sharedPrefManager = SharedPrefManager(context)
        getUser(sharedPrefManager.getUserId())
        userId = sharedPrefManager.getUserId()
        intrface = context as MainActivityInterface

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


    private fun fetchTasks() {
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
        binding.apply {
            ivProfilePic.setOnClickListener { ActivityNavigator.startActivity(context, ActivityProfile::class.java,it) }
            viewTasksAll.setOnClickListener { intrface.switchTab(R.id.fragment_task) }
            detailTask.setOnClickListener { intrface.switchTab(R.id.fragment_task) }
            viewMoods.setOnClickListener { intrface.switchTab(R.id.fragment_mood) }
            tvReminder.setOnClickListener { intrface.switchTab(R.id.fragment_mood) }
        }
        setupLineChart()
        updateTaskProgress()


    }


    private fun getUser(userId: String?) {
        if (userId.isNullOrEmpty()) {
            Toast.makeText(context, "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val sharedPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPref.getString("userId", null)
        Toast.makeText(context, "debug ; " + userId, Toast.LENGTH_SHORT).show()


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
                            Toast.makeText(context, "User: ${user.userName}", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Failed to parse user", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context, "No user found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupLineChart() {
        binding.apply {
            // Get tasks grouped by week
            val taskCounts = getTasksGroupedByWeek()

            val yearLabels = arrayOf("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th")

            // Create the chart entries from the task counts
            val entries = mutableListOf<Entry>()
            taskCounts.forEachIndexed { index, count ->
                entries.add(Entry(index.toFloat(), count.toFloat()))
            }

            val dataSet = LineDataSet(entries, "Tasks Per Week").apply {
                color = ContextCompat.getColor(context, R.color.chart_line)
                valueTextColor = ContextCompat.getColor(context, R.color.text_primary)
                lineWidth = 2.5f
                setCircleColor(ContextCompat.getColor(context, R.color.chart_line))
                circleRadius = 4f
                circleHoleRadius = 2f
                setDrawCircleHole(true)
                valueTextSize = 10f
                mode = LineDataSet.Mode.LINEAR
                setDrawFilled(false)
                setDrawValues(false)
                setDrawHorizontalHighlightIndicator(false)
                setDrawVerticalHighlightIndicator(false)
            }

            with(lineChart) {
                data = LineData(dataSet)

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
                    yOffset = 8f
                }

                axisLeft.apply {
                    granularity = 5f // Set granularity to steps of 5
                    axisMinimum = 0f
                    axisMaximum = 18f // Maximum to represent up to 18 tasks
                    axisLineColor = ContextCompat.getColor(context, R.color.axis_line)
                    textColor = ContextCompat.getColor(context, R.color.text_primary)
                    textSize = 11f
                    setDrawZeroLine(false)
                    setDrawGridLines(true)
                    gridColor = ContextCompat.getColor(context, R.color.grid_line)
                    gridLineWidth = 0.5f
                    setDrawAxisLine(false)
                }

                axisRight.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(false)

                setDrawBorders(false)
                setBorderWidth(0f)
                setNoDataText("No data available")
                setNoDataTextColor(ContextCompat.getColor(context, R.color.text_secondary))

                setBackgroundColor(Color.TRANSPARENT)
                setDrawGridBackground(false)

                xAxis.setDrawAxisLine(true)
                axisLeft.setDrawAxisLine(false)

                extraLeftOffset = 5f
                extraRightOffset = 15f
                extraBottomOffset = 5f

                animateY(800, Easing.Linear)

                invalidate()
            }
        }
    }


    private fun getTasksGroupedByWeek(): List<Int> {
        val tasks = sharedPrefManager.getTasks()
        val weekCounts = MutableList(8) { 0 }

        // Get the current calendar week
        val currentCalendar = Calendar.getInstance()
        val currentWeekOfYear = currentCalendar.get(Calendar.WEEK_OF_YEAR)

        tasks?.forEach { task ->
            val taskTimestamp = task.createdAt
            val taskDate = taskTimestamp?.toDate()

            taskDate?.let {
                val calendar = Calendar.getInstance().apply {
                    time = taskDate
                }

                val taskWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)

                // Calculate relative week (how many weeks ago)
                val weekDifference = currentWeekOfYear - taskWeekOfYear

                // Only count tasks from the current week (0) and 7 weeks prior (1-7)
                if (weekDifference in 0..7) {
                    weekCounts[weekDifference] += 1
                }
            }
        }

        // Reverse the list so most recent week is last (if that's what you need)
        return weekCounts
    }


    private fun updateTaskProgress() {
        val tasks = sharedPrefManager.getTasks() ?: emptyList()

        // Calendar instances
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        // Filter tasks for the current month
        val tasksThisMonth = tasks.filter { task ->
            val taskDate = task.createdAt?.toDate()
            if (taskDate != null) {
                val taskCalendar = Calendar.getInstance().apply {
                    time = taskDate
                }
                taskCalendar.get(Calendar.MONTH) == currentMonth &&
                        taskCalendar.get(Calendar.YEAR) == currentYear
            } else {
                false
            }
        }

        // Filter tasks for today
        val todayTasks = tasksThisMonth.filter { task ->
            val taskDate = task.createdAt?.toDate()
            if (taskDate != null) {
                val taskCalendar = Calendar.getInstance().apply {
                    time = taskDate
                }
                taskCalendar.get(Calendar.DAY_OF_MONTH) == currentDay
            } else {
                false
            }
        }

        // Filter tasks for this week
        val thisWeekTasks = tasks.filter { task ->
            val taskDate = task.createdAt?.toDate()
            if (taskDate != null) {
                val taskCalendar = Calendar.getInstance().apply {
                    time = taskDate
                }
                taskCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeek &&
                        taskCalendar.get(Calendar.YEAR) == currentYear
            } else {
                false
            }
        }

        // Count tasks
        val totalTasksCount = tasksThisMonth.size
        val completedTasksCount = tasksThisMonth.count { it.status == "completed" }

        val todayTotalCount = todayTasks.size
        val todayCompletedCount = todayTasks.count { it.status == "completed" }

        val weeklyTotalCount = thisWeekTasks.size
        val weeklyCompletedCount = thisWeekTasks.count { it.status == "completed" }

        // Calculate progress percentages
        val monthlyProgressPercentage = if (totalTasksCount > 0) {
            (completedTasksCount * 100) / totalTasksCount
        } else {
            0
        }

        val todayProgressPercentage = if (todayTotalCount > 0) {
            (todayCompletedCount * 100) / todayTotalCount
        } else {
            0
        }

        val weeklyProgressPercentage = if (weeklyTotalCount > 0) {
            (weeklyCompletedCount * 100) / weeklyTotalCount
        } else {
            0
        }

        // Update UI elements using binding
        binding.tasks.text = completedTasksCount.toString()
        binding.totalTask.text = "/$totalTasksCount"
        binding.taskProgressBar.progress = monthlyProgressPercentage

        // Update today's tasks
        binding.todayTasks.text = "$todayCompletedCount/$todayTotalCount"
        binding.todayProgessbar.progress = todayProgressPercentage

        // Update weekly progress
        binding.weeklyProgress.text = "$weeklyProgressPercentage%"
        binding.weeklyProgressBar.progress = weeklyProgressPercentage
    }


}
