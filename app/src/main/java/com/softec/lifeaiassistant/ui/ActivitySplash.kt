package com.softec.lifeaiassistant.ui

import android.content.Context
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.softec.lifeaiassistant.customClasses.ActivityNavigator

import com.softec.lifeaiassistant.databinding.ActivitySplashBinding
import com.softec.lifeaiassistant.viewModel.SplashActivityViewModel


class ActivitySplash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            img.animate().scaleX(1f).scaleY(1f).translationY(0F).setDuration(1500)
                .setInterpolator(AccelerateDecelerateInterpolator()).start()
            tvAppName.animate().alpha(1f).translationY(0F).setDuration(1000)
                .setInterpolator(OvershootInterpolator()).setStartDelay(1500).start()
            tvTagLine.animate().alpha(1f).translationY(0F).setDuration(1000)
                .setInterpolator(OvershootInterpolator()).setStartDelay(1500).start()
        }


        viewModel.startTimer(3000, 3000)

        viewModel.timerFinished.observe(this) { timerFinished ->
            if (timerFinished) {
                val status = userOnBoardingStatus()
                when (status) {
                    1 -> ActivityNavigator.startActivity(this, MainActivity::class.java, binding.animator)
                    else -> ActivityNavigator.startActivity(this, AuthenticationActivity::class.java,binding.animator)
                }
                finish()
            }
        }
    }

    private fun userOnBoardingStatus(): Int {
        return getSharedPreferences("Logged", Context.MODE_PRIVATE).getInt("isLogged", 0)
    }

}