package com.softec.lifeaiassistant.viewModel
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashActivityViewModel : ViewModel() {


    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished: LiveData<Boolean> get() = _timerFinished


    private var countDownTimer: CountDownTimer? = null


    fun startTimer(duration: Long, interval: Long) {
        countDownTimer = object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                _timerFinished.value = true
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}