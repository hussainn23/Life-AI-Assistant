package com.softec.lifeaiassistant.customClasses
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softec.lifeaiassistant.repository.AuthRepository
import com.softec.lifeaiassistant.viewModel.LoginViewModel

class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
