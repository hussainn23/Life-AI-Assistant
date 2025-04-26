package com.softec.lifeaiassistant.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.softec.lifeaiassistant.models.ModelUser
import java.util.Base64
import kotlin.experimental.xor

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    fun createUser(
        name: String,
        email: String,
        password: String,
        callback: (AuthenticationResponses<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid ?: return@addOnCompleteListener
                    val passwordHash = generateHash(password, "user")
                    val userData = ModelUser(userId, name, email, passwordHash, "")

                    // Update the user's displayName
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user.updateProfile(profileUpdates).addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Save user details in the database
                            database.child("users").child(userId).setValue(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        user.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    callback(AuthenticationResponses.Success("Please verify your email."))
                                                } else {
                                                    callback(AuthenticationResponses.Error("Failed to send verification email."))
                                                }
                                            }
                                    } else {
                                        callback(AuthenticationResponses.Error("Failed to register user in database."))
                                    }
                                }
                        } else {
                            callback(AuthenticationResponses.Error("Failed to update user profile."))
                        }
                    }
                } else {
                    callback(AuthenticationResponses.Error("Registration Failed: ${task.exception?.message}"))
                }
            }
    }

    private fun generateHash(input: String, key: String): String {
        val inputBytes = input.toByteArray(Charsets.UTF_8)
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val outputBytes = ByteArray(inputBytes.size)

        for (i in inputBytes.indices) {
            outputBytes[i] = (inputBytes[i] xor keyBytes[i % keyBytes.size])
        }
        return Base64.getEncoder().encodeToString(outputBytes)
    }

    fun loginUser(email: String, password: String, callback: (AuthResult) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        callback(AuthResult.Success)
                    } else {
                        callback(AuthResult.EmailNotVerified)
                    }
                } else {
                    callback(AuthResult.Failure(task.exception?.message ?: "Unknown error"))
                }
            }
    }

    fun sendPasswordResetEmail(email: String, callback: (AuthResult) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(AuthResult.Success)
                } else {
                    callback(AuthResult.Failure(task.exception?.message ?: "Unknown error"))
                }
            }
    }

    suspend fun getOnBoardingStatusFromFirebase(): Boolean {
        val user = auth.currentUser ?: return false
        Log.e(TAG, "getOnBoardingStatusFromFirebase: User is not Null -> ${auth.uid}")
        val userId = user.uid
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("UsersResponses")
            .child(userId)

        return try {
            val snapshot = databaseRef.get().await()
            Log.e(TAG, "getOnBoardingStatusFromFirebase: snapShot : ${snapshot.exists()}")
            snapshot.exists()
        } catch (e: Exception) {
            Log.e(TAG, "getOnBoardingStatusFromFirebase: snapShot : Exception Running")
            false
        }
    }

}


sealed class AuthenticationResponses<out T> {
    data class Success<out T>(val data: T) : AuthenticationResponses<T>()
    data class Error(val message: String) : AuthenticationResponses<Nothing>()
    data object Loading : AuthenticationResponses<Nothing>()
}

