package com.softec.lifeaiassistant.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softec.lifeaiassistant.R

class MainActivityViewModel : ViewModel() {

    // LiveData to track the selected bottom navigation item
    private val _selectedNavItem = MutableLiveData<Int>()
    val selectedNavItem: LiveData<Int> get() = _selectedNavItem

    // LiveData to track the visibility of each fragment
    private val _isHomeFragmentVisible = MutableLiveData<Boolean>()
    val isHomeFragmentVisible: LiveData<Boolean> get() = _isHomeFragmentVisible

    private val _isTaskFragmentVisible = MutableLiveData<Boolean>()
    val isTaskFragmentVisible: LiveData<Boolean> get() = _isTaskFragmentVisible

    private val _isReminderFragmentVisible = MutableLiveData<Boolean>()
    val isReminderFragmentVisible: LiveData<Boolean> get() = _isReminderFragmentVisible

    private val _isMoodFragmentVisible = MutableLiveData<Boolean>()
    val isMoodFragmentVisible: LiveData<Boolean> get() = _isMoodFragmentVisible

    private val _isScheduleFragmentVisible = MutableLiveData<Boolean>()
    val isScheduleFragmentVisible: LiveData<Boolean> get() = _isScheduleFragmentVisible

    // LiveData to handle the double back press logic
    private val _backPressedTime = MutableLiveData<Long>()
    val backPressedTime: LiveData<Long> get() = _backPressedTime

    // Constant for double back press time interval
    companion object {
        const val DOUBLE_BACK_TIME = 2000L
    }

    init {
        // Initialize default values
        _selectedNavItem.value = R.id.fragment_home
        _isHomeFragmentVisible.value = true
        _isTaskFragmentVisible.value = false
        _isReminderFragmentVisible.value = false
        _isMoodFragmentVisible.value = false
        _isScheduleFragmentVisible.value = false
        _backPressedTime.value = 0L
    }

    // Function to handle bottom navigation item selection
    fun onNavItemSelected(itemId: Int) {
        _selectedNavItem.value = itemId

        // Reset all fragment visibility to false
        _isHomeFragmentVisible.value = false
        _isTaskFragmentVisible.value = false
        _isReminderFragmentVisible.value = false
        _isMoodFragmentVisible.value = false
        _isScheduleFragmentVisible.value = false

        // Show the selected fragment
        when (itemId) {
            R.id.fragment_home -> _isHomeFragmentVisible.value = true
            R.id.fragment_task -> _isTaskFragmentVisible.value = true
            R.id.fragment_reminder -> _isReminderFragmentVisible.value = true
            R.id.fragment_mood -> _isMoodFragmentVisible.value = true
            R.id.fragment_schedule -> _isScheduleFragmentVisible.value = true
        }
    }

    // Function to handle back press logic
    fun onBackPressed(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (_selectedNavItem.value != R.id.fragment_home) {
            onNavItemSelected(R.id.fragment_home)
            true
        } else {
            if (_backPressedTime.value!! + DOUBLE_BACK_TIME > currentTime) {
                false
            } else {
                _backPressedTime.value = currentTime
                true
            }
        }
    }
}
