package com.softec.lifeaiassistant.customClasses
sealed class AuthResult {
    data object Success : AuthResult()
    data object EmailNotVerified : AuthResult()
    data class Failure(val message: String) : AuthResult()
}
