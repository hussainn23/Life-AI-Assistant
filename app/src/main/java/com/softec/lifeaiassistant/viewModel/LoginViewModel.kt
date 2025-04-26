package com.softec.lifeaiassistant.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softec.lifeaiassistant.repository.AuthRepository


class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> get() = _loginResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun login(email: String, password: String) {
        _loading.value = true

        authRepository.loginUser(email, password) { result ->
            _loading.value = false
            _loginResult.value = result
        }
    }

    private val _resetStatus = MutableLiveData<AuthResult>()
    val resetStatus: LiveData<AuthResult> get() = _resetStatus

    private val _loadingDialog = MutableLiveData<Boolean>()
    val loadingDialog: LiveData<Boolean> get() = _loadingDialog

    fun resetPassword(email: String) {
        _loadingDialog.value = true
        authRepository.sendPasswordResetEmail(email) { result ->
            _loadingDialog.value = false
            _resetStatus.value = result
        }
    }

    suspend fun getOnBoardingStatusFromFirebase(): Boolean {
        return authRepository.getOnBoardingStatusFromFirebase()
    }


}
