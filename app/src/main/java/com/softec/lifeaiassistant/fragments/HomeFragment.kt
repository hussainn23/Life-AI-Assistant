package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.databinding.HomeFragmentBinding
import com.softec.lifeaiassistant.databinding.LayoutFragmentHomeBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {
//        home_fragment.xml

    private lateinit var binding: HomeFragmentBinding


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
        binding = HomeFragmentBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        setupUi()
        setupLineChart()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUi() {
        binding.apply {
            greetingText.text = getGreetingMessage() + ", UserName"
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
            val formattedDate = currentDate.format(formatter)
            dateText.text = formattedDate
        }
    }

    private fun setupLineChart() {
        // Fixed data matching the reference image style
        val yearLabels = arrayOf("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018")
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
        with(binding.lineChart) {
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


    private fun getGreetingMessage(): String {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
