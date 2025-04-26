package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.databinding.LayoutFragmentHomeBinding
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.viewModel.TaskViewModel
import kotlinx.coroutines.launch

class TaskFragment(private val context: AppCompatActivity) : AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var binding: LayoutFragmentHomeBinding
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate() {
        taskViewModel = ViewModelProvider(context)[TaskViewModel::class.java]

        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {}

                override fun onFinish() {
                    // Call the suspend function from a coroutine
                    context.lifecycleScope.launch {
                     //   GetContent()
                    }
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
        binding = LayoutFragmentHomeBinding.inflate(context.layoutInflater, base, false)
        binding.root.apply {
            alpha = 0f
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
    }

//    private suspend fun GetContent() {
//        try {
//            // Get response from ViewModel
//            val response = taskViewModel.getTaskData("Tomorrow I will leave for Lahore. From this statement, extract: Note Name, Note Category, Due Date, Checklist, and Task Type")
//            Log.d(ContentValues.TAG, "GetContent: $response")
//
//            // extract data from response  the name , take type , subcategory, due date
//
//            //taskContent, dueDate(if empty then parse above),priority,reminder
//
//            taskViewModel.saveData(//taskModel)
//
//
//
//
//
//
//
//
//
//        } catch (e: Exception) {
//            // Handle error if any
//            Log.e(ContentValues.TAG, "Error in GetContent: ${e.message}")
//        }
//    }
}
