package com.softec.lifeaiassistant.viewModel
import android.os.Build
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softec.lifeaiassistant.customClasses.AuthenticationResponses
import com.softec.lifeaiassistant.repository.AuthRepository


class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _signupState = MutableLiveData<AuthenticationResponses<String>>()
    val signupState: LiveData<AuthenticationResponses<String>> = _signupState

    fun createUser(name: String, email: String, password: String) {

        _signupState.value = AuthenticationResponses.Loading
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            authRepository.createUser(name, email, password) { result ->
                _signupState.value = result
            }
        }
    }

}