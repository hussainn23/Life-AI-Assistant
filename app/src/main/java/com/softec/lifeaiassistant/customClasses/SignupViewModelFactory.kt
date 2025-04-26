package com.softec.lifeaiassistant.customClasses
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softec.lifeaiassistant.repository.AuthRepository
import com.softec.lifeaiassistant.viewModel.SignupViewModel

class SignupViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
