package com.softec.lifeaiassistant.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

import com.google.gson.reflect.TypeToken
import com.softec.lifeaiassistant.models.ModelUser
import com.softec.lifeaiassistant.models.TaskModel

class SharedPrefManager(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val gson = Gson()



    fun saveId(id: String) {
        editor.putString("userId", id).apply()
    }

    fun getId(): String? =
        sharedPref.getString("userId", null)

    fun saveDocId(id: String) {
        editor.putString("docId", id).apply()
    }
    fun saveUser(user: ModelUser) {
        editor.putString("user", Gson().toJson(user))
        editor.apply()
    }


    fun getUser(): ModelUser? {
        val json = sharedPref.getString("user", null)
        return if (json.isNullOrEmpty()) {
            null
        } else {
            try {
                Gson().fromJson(json, ModelUser::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null
            }
        }
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

    fun getUserId(): String? =
        sharedPref.getString("userId", "")

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


    fun saveTasks(list: List<TaskModel>) {
        val json = gson.toJson(list)
        editor.putString("task", json)
        editor.apply()
    }

    fun getTasks(): List<TaskModel>? {
        val json = sharedPref.getString("task", null)
        val type = object : TypeToken<List<TaskModel>>() {}.type
        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }


}
