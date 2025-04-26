package com.softec.lifeaiassistant.customClasses
import android.app.Application

import com.google.firebase.FirebaseApp
import com.softec.lifeaiassistant.geminiClasses.GetChatResponseText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LifeAIAssistant : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            initializeFirebase()
        }


    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this@LifeAIAssistant)
        GetChatResponseText.init(applicationContext)
    }

}
