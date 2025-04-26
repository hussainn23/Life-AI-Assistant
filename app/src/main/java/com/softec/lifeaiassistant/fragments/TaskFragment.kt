package com.softec.lifeaiassistant.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
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
import java.util.*

class TaskFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: FragmentTaskBinding
    private var dialogBinding: DialogAddTaskBinding? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private val RECORD_AUDIO_PERMISSION_CODE = 101

    // Permission launcher for recording audio
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate() {
        try {
            galleryLauncher = context.activityResultRegistry.register(
                "galleryPicker",
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    if (selectedImageUri != null) {
                        extractTextFromImage(selectedImageUri)
                    }
                }
            }

            // Register permission launcher
            requestPermissionLauncher = context.activityResultRegistry.register(
                "requestPermission",
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action.
                    startSpeechRecognition()
                } else {
                    // Explain to the user that the feature is unavailable
                    showToast("Permission denied. Speech recognition cannot work without microphone access.")
                }
            }

            // Initialize everything else after a brief delay
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {}

                override fun onFinish() {
                    initiateLayout()
                }
            }.start()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "initiateData: ${e.message}")
        }
    }

    private fun initiateLayout() {
        settingUpBinding()
    }

    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = FragmentTaskBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = 0f
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
            dialogBinding = DialogAddTaskBinding.inflate(context.layoutInflater)
            dialog.setContentView(dialogBinding!!.root)
            dialogBinding?.apply {
                layHrs.setOnClickListener {
                    showDatePickerDialog(etHrs)
                }

                ivCamera.setOnClickListener {
                    openGallery()
                }

                ivMicrophone.setOnClickListener {
                    checkPermissionAndStartSpeechRecognition()
                }

                btnSave.setOnClickListener {

                }


                ivBack.setOnClickListener { dialog.dismiss() }
                btnCancel.setOnClickListener { dialog.dismiss() }
            }
            dialog.show()
        }
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

            val selectedDate = arrayOf(LocalDate.now()) // Set the current date as the default selected date

            weekCalendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
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
            }

            // Scroll to the week of the current date when the activity loads
            weekCalendarView.post { weekCalendarView.scrollToWeek(selectedDate[0]) }
        }
    }

    private fun showDatePickerDialog(etHrs: EditText) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
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

    private fun openGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error opening gallery: ${e.message}")
        }
    }

    private fun extractTextFromImage(imageUri: Uri) {
        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)

            val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val extractedText = visionText.text
                    Log.d("MLKit", "Extracted text: $extractedText")
                    dialogBinding?.etServiceName?.setText(extractedText)
                }
                .addOnFailureListener { e ->
                    Log.e("MLKit", "Failed to recognize text: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("MLKit", "Error preparing image: ${e.message}")
        }
    }

    // Permission check for speech recognition
    private fun checkPermissionAndStartSpeechRecognition() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Launch the permission request
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            startSpeechRecognition()
        }
    }

    // Enhanced Error Handling for Speech Recognition
    private fun handleSpeechRecognitionError(errorCode: Int) {
        when (errorCode) {
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                showToast("Network timeout. Try again.")
            }
            SpeechRecognizer.ERROR_NETWORK -> {
                showToast("Network error. Check your internet connection.")
            }
            SpeechRecognizer.ERROR_AUDIO -> {
                showToast("Audio error. Please ensure your microphone is working.")
            }
            SpeechRecognizer.ERROR_CLIENT -> {
                showToast("Client error. Restart the app and try again.")
            }
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                showToast("Speech timeout. Please speak again.")
            }
            SpeechRecognizer.ERROR_NO_MATCH -> {
                showToast("No speech match found. Please speak clearly.")
            }
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                showToast("Recognizer is busy. Please wait.")
                // Try to release and reinitialize the recognizer
                releaseAndReinitializeSpeechRecognizer()
            }
            else -> {
                showToast("Unknown error occurred. Error code: $errorCode")
            }
        }
        // Stop speech recognition to avoid future errors
        stopSpeechRecognition()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Method to start the speech-to-text process
    private fun startSpeechRecognition() {
        try {
            // Check if speech recognition is available
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                // Create or reinitialize the speech recognizer if needed
                if (speechRecognizer == null) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                    setupSpeechRecognitionListener()
                }

                // Create an Intent for speech recognition
                val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please speak now")

                // Visual feedback that speech recognition is active
                showToast("Listening... Please speak now")

                // Start listening for speech input
                speechRecognizer?.startListening(recognizerIntent)
            } else {
                // Show a message if speech recognition is not available
                showToast("Speech recognition is not available on this device.")
            }
        } catch (e: Exception) {
            // Handle any exceptions that may occur during speech recognition setup
            Log.e("SpeechRecognizer", "Error starting speech recognition: " + e.message)
            showToast("Error starting speech recognition: " + e.message)
            releaseAndReinitializeSpeechRecognizer()
        }
    }

    private fun setupSpeechRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizer", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizer", "Beginning of speech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Can be used to show a visual indicator of voice volume
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SpeechRecognizer", "Buffer received")
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizer", "End of speech")
            }

            override fun onError(error: Int) {
                Log.e("SpeechRecognizer", "Error occurred with code: $error")
                handleSpeechRecognitionError(error)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizer", "Recognized text: $recognizedText")
                    dialogBinding?.etServiceName?.setText(recognizedText)
                    showToast("Text recognized successfully")
                } else {
                    showToast("No speech recognized. Please try again.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognizer", "Partial result: $recognizedText")
                    dialogBinding?.etServiceName?.setText(recognizedText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SpeechRecognizer", "Event type: $eventType")
            }
        })
    }

    // Stop listening if needed (in case of interruption or completion)
    private fun stopSpeechRecognition() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error stopping speech recognition: ${e.message}")
        }
    }

    private fun releaseAndReinitializeSpeechRecognizer() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            // Wait a bit before reinitializing to ensure clean state
            Handler(Looper.getMainLooper()).postDelayed({
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                setupSpeechRecognitionListener()
            }, 200)
        } catch (e: Exception) {
            Log.e("SpeechRecognizer", "Error reinitializing speech recognizer: ${e.message}")
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Properly release the speech recognizer to avoid memory leaks
//        if (speechRecognizer != null) {
//            speechRecognizer?.destroy()
//            speechRecognizer = null
//        }
//    }
}