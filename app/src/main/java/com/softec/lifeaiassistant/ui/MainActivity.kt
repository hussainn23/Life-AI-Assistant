package com.softec.lifeaiassistant.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragment
import com.softec.lifeaiassistant.databinding.ActivityMainBinding
import com.softec.lifeaiassistant.fragments.HomeFragment
import com.softec.lifeaiassistant.fragments.MoodFragment
import com.softec.lifeaiassistant.fragments.ReminderFragment
import com.softec.lifeaiassistant.fragments.SummarizerFragment
import com.softec.lifeaiassistant.fragments.TaskFragment
import com.softec.lifeaiassistant.viewModel.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var fragment_home: AppFragment
    private lateinit var fragment_task: AppFragment
    private lateinit var fragment_reminder: AppFragment
    private lateinit var fragment_mood: AppFragment
    private lateinit var fragment_schedule: AppFragment
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var taskFragment: TaskFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImageUri = result.data?.data
                taskFragment.onImageSelected(selectedImageUri)
            }
        }
        taskFragment = TaskFragment(this,imagePickerLauncher)

        setupFragments()

        binding.btmNav.setOnItemSelectedListener {
            viewModel.onNavItemSelected(it.itemId)
            true
        }
        viewModel.selectedNavItem.observe(this) { itemId ->
            binding.btmNav.selectedItemId = itemId
        }
        binding.btmNav.setOnItemReselectedListener { }


        viewModel.isHomeFragmentVisible.observe(this) { isVisible ->
            fragment_home.visible(isVisible)
        }
        viewModel.isTaskFragmentVisible.observe(this) { isVisible ->
            fragment_task.visible(isVisible)
        }
        viewModel.isReminderFragmentVisible.observe(this) { isVisible ->
            fragment_reminder.visible(isVisible)
        }
        viewModel.isMoodFragmentVisible.observe(this) { isVisible ->
            fragment_mood.visible(isVisible)
        }
        viewModel.isScheduleFragmentVisible.observe(this) { isVisible ->
            fragment_schedule.visible(isVisible)
        }

        fragment_home.visible(true)

    }

    private fun setupFragments() {
        fragment_home = findViewById(R.id.fragment_home)
        fragment_task = findViewById(R.id.fragment_task)
        fragment_reminder = findViewById(R.id.fragment_reminder)
        fragment_mood = findViewById(R.id.fragment_mood)
        fragment_schedule = findViewById(R.id.fragment_schedule)

        fragment_home.onAppFragmentLoader = HomeFragment(this)
        fragment_task.onAppFragmentLoader = taskFragment
        fragment_reminder.onAppFragmentLoader = ReminderFragment(this)
        fragment_mood.onAppFragmentLoader = MoodFragment(this)
        fragment_schedule.onAppFragmentLoader = SummarizerFragment(this)

    }


    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (!viewModel.onBackPressed()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }



}