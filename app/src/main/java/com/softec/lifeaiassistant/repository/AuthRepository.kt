package com.softec.lifeaiassistant.repository

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.softec.lifeaiassistant.customClasses.AuthResult
import com.softec.lifeaiassistant.customClasses.AuthenticationResponses
import com.softec.lifeaiassistant.models.ModelUser
import kotlinx.coroutines.tasks.await
import java.util.Base64
import kotlin.experimental.xor

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
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
                            // Save user details in Firestore
                            firestore.collection("Users")
                                .document(userId)
                                .set(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        callback(AuthenticationResponses.Success("Registered successfully!."))
                                        /*user.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    callback(AuthenticationResponses.Success("Please verify your email."))
                                                } else {
                                                    callback(AuthenticationResponses.Error("Failed to send verification email."))
                                                }
                                            }*/
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                            /*&& user.isEmailVerified*/
                    if (user != null) {
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
        val documentRef = firestore.collection("UsersResponses").document(userId)

        return try {
            val snapshot = documentRef.get().await()
            Log.e(TAG, "getOnBoardingStatusFromFirebase: snapShot exists: ${snapshot.exists()}")
            snapshot.exists()
        } catch (e: Exception) {
            Log.e(TAG, "getOnBoardingStatusFromFirebase: Exception running: ${e.message}")
            false
        }
    }

}


