package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.adapters.ReminderAdapter
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.databinding.FragmentSchedulingsBinding
import com.softec.lifeaiassistant.databinding.LayoutFragmentHomeBinding
import com.softec.lifeaiassistant.utils.SharedPrefManager
import kotlin.math.truncate

class ReminderFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var binding: FragmentSchedulingsBinding
    private lateinit var sharedPref : SharedPrefManager

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
        binding = FragmentSchedulingsBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        sharedPref = SharedPrefManager(context)
        Log.e("TAG", "settingUpBinding: "+sharedPref.getTasks()!!.size )

        binding.reminderRecyclerView.adapter = ReminderAdapter(sharedPref.getTasks()!!)




    }
}

