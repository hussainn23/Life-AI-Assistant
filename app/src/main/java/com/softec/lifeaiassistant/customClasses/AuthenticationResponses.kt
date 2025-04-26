package com.softec.lifeaiassistant.customClasses

sealed class AuthenticationResponses<out T> {
    data class Success<out T>(val data: T) : AuthenticationResponses<T>()
    data class Error(val message: String) : AuthenticationResponses<Nothing>()
    data object Loading : AuthenticationResponses<Nothing>()
}
