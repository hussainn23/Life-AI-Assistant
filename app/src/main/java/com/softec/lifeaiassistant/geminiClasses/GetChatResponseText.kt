package com.softec.lifeaiassistant.geminiClasses

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.softec.lifeaiassistant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object GetChatResponseText {

    private lateinit var chat: Chat
    private var apiKey: String? = null

    interface ResponseCallback {
        fun onSuccess(response: String)
        fun onError(errorMessage: String)
    }

    fun init(context: Context) {
        if (apiKey == null) {
            apiKey = context.applicationContext.getString(R.string.apiKey)
        }
    }

    suspend fun getResponse(text: String): String {
        return withContext(Dispatchers.IO) {
            if (apiKey == null) {
                throw IllegalStateException("API Key is not initialized. Call init(context) first.")
            }

            Log.e("TAG", "getResponse: API Key : $apiKey")
            val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey!!)
            chat = generativeModel.startChat(history = listOf())

            try {
                val result = chat.sendMessage(text)
                result.text
            } catch (e: Exception) {
                Log.e("TAG", "Error during chat: ${e.printStackTrace()}")
                throw Exception("An error occurred. Please try again.")
            }.toString()
        }
    }

}

/*
* GetChatResponseText chatResponseText = new GetChatResponseText();

        chatResponseText.getResponse(promptText, new GetChatResponseText.ResponseCallback() {
            @Override public void onSuccess(@NonNull String response) {
                chatTextList.add(new ChatDataClass(response,true));
                adapter.notifyItemInserted(chatTextList.size()-1);
                rcvChat.scrollToPosition(chatTextList.size()-1);
                progressIndicator.setVisibility(View.GONE);
            }

            @Override public void onError(@NonNull String errorMessage) {
                Toast.makeText(GPTChat.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Chat Response Error : " + errorMessage );
                progressIndicator.setVisibility(View.GONE);
            }
        });*/