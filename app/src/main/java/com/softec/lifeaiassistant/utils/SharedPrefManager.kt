package com.softec.lifeaiassistant.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun saveId(id: String) {
        editor.putString("userId", id).apply()
    }

    fun getId(): String? =
        sharedPref.getString("userId", null)

    fun saveDocId(id: String) {
        editor.putString("docId", id).apply()
    }

    fun getDocId(): String? =
        sharedPref.getString("docId", null)

    fun saveLogin() {
        editor.putString(Constants.LOGIN, "login").apply()
    }

    fun checkLogin(): Boolean =
        sharedPref.getString(Constants.LOGIN, null) == "login"

    fun saveUserName(name: String) {
        editor.putString("userName", name).apply()
    }

    fun getUserName(): String? =
        sharedPref.getString("userName", "")

    fun saveUserEmail(email: String) {
        editor.putString("userEmail", email).apply()
    }

    fun getUserEmail(): String? =
        sharedPref.getString("userEmail", "")

    // ─── NEW for password flow ───
    fun saveUserPassword(password: String) {
        editor.putString("userPassword", password).apply()
    }

    fun getStoredPassword(): String? =
        sharedPref.getString("userPassword", null)

    // ─── CLEAR ALL on logout ───
    fun clearUserData() {
        editor.clear().apply()
    }
}
