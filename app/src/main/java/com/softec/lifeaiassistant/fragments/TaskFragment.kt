package com.softec.lifeaiassistant.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.view.WeekDayBinder
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.DayViewContainer
import com.softec.lifeaiassistant.databinding.DialogAddTaskBinding
import com.softec.lifeaiassistant.databinding.FragmentTaskBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale


class TaskFragment(private val context: AppCompatActivity, private val imagePickerLauncher: ActivityResultLauncher<Intent>) :
    AppFragmentLoader(R.layout.layout_fragment_home) {



    private lateinit var binding: FragmentTaskBinding


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

    fun onImageSelected(selectedImageUri: Uri?) {
        if (selectedImageUri != null) {
            Toast.makeText(context, "Image Selected: $selectedImageUri", Toast.LENGTH_SHORT).show()
        }
    }


    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = FragmentTaskBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        doWork()
    }


    private fun doWork() {
        calendarViewSetting()
        binding.fabAddTask.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            val dialogBinding = DialogAddTaskBinding.inflate(context.layoutInflater)
            dialog.setContentView(dialogBinding.root)
            dialogBinding.apply {
                layHrs.setOnClickListener {
                    showDatePickerDialog(etHrs)
                }
                ivCamera.setOnClickListener { openGallery() }
                etHrs.setOnClickListener {
                    showDatePickerDialog(etHrs)
                }
                ivBack.setOnClickListener {dialog.dismiss()}
                btnCancel.setOnClickListener {dialog.dismiss()}
            }
            dialog.show()
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)

        /*ImagePicker.with(context)
            .galleryOnly()
            .crop()
            .start(GALLERY_IMAGE_PICK_REQUEST_CODE)*/
    }



    @SuppressLint("NewApi")
    private fun calendarViewSetting() {
        binding.apply {

            val currentDate = LocalDate.now()
            val currentMonth = YearMonth.now()
            currentMonth.atDay(1) // Start from the first day of the current month
            val endDate = currentMonth.plusMonths(1).atEndOfMonth() // End at the end of next month

            weekCalendarView.setup(
                currentDate,
                endDate,
                WeekFields.of(Locale.getDefault()).firstDayOfWeek
            )
            weekCalendarView.scrollToWeek(currentDate)

            val selectedDate =
                arrayOf(LocalDate.now()) // Set the current date as the default selected date

            weekCalendarView.dayBinder = (object : WeekDayBinder<DayViewContainer> {
                override fun bind(container: DayViewContainer, day: WeekDay) {
                    val dayName = day.date.dayOfWeek.name.substring(0, 3) // e.g., "MON", "TUE"
                    container.dayNameText.text = dayName

                    container.textView.text = day.date.dayOfMonth.toString()
                    container.date = day.date

                    // Default states for all dates
                    container.dayNameText.setTextColor(context.getColor(R.color.textColor))
                    container.textView.setTextColor(context.getColor(R.color.textColorSecondary))
                    container.base.setBackgroundResource(R.drawable.filled_10dp_round_with_states_effect)

                    // Highlight the selected date
                    if (day.date == selectedDate[0]) {
                        container.dayNameText.setTextColor(context.getColor(R.color.white))
                        container.textView.setTextColor(context.getColor(R.color.white))
                        container.base.setBackgroundResource(R.drawable.bg_primary_color_10dp)
                    }

                    // Set the click listener for dates
                    container.base.setOnClickListener {
                        val previousSelectedDate = selectedDate[0]
                        selectedDate[0] = day.date

                        weekCalendarView.notifyDateChanged(previousSelectedDate)
                        weekCalendarView.notifyDateChanged(selectedDate[0])
                    }
                }

                override fun create(view: View): DayViewContainer {
                    return DayViewContainer(view)
                }
            })

            // Scroll to the week of the current date when the activity loads
            weekCalendarView.post { weekCalendarView.scrollToWeek(selectedDate[0]) }
        }

    }

    private fun showDatePickerDialog(etHrs: EditText) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context, // or use requireContext() if inside a Fragment
            { _, year, month, dayOfMonth ->
                // month is zero-based, so add 1
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                etHrs.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict to today and future dates
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }


    companion object {
        private const val GALLERY_IMAGE_PICK_REQUEST_CODE = 101
        private const val CAMERA_IMAGE_PICK_REQUEST_CODE = 102
    }
}
