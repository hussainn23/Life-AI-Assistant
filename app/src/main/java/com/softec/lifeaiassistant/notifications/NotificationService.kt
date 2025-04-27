package com.softec.lifeaiassistant.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.ui.MainActivity
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private val channelId = "AssignId"
    private val tag = "NotificationService"

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", MODE_PRIVATE)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "New token: $token")

        val id = sharedPreferences.getString("id", null)
        if (!id.isNullOrEmpty()) {
            firestore.collection("users").document(id)
                .update("deviceToken", token)
                .addOnSuccessListener {
                    Log.d(tag, "Token successfully updated for user: $id")
                }
                .addOnFailureListener { e ->
                    Log.e(tag, "Failed to update token for user: $id", e)
                }
        } else {
            Log.e(tag, "User ID not found in SharedPreferences")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(tag, "Message received: ${message.data}")

        val title = message.data["title"] ?: "New Notification"
        val body = message.data["body"] ?: "You have a new notification"

        // Create a pending intent to open the app when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true) // Close notification when clicked
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        // Generate a unique ID for each notification
        val notificationId = Random.nextInt(100000)
        notificationManager.notify(notificationId, notification)

        // Store the notification (if needed)
        storeNotification(title, body)
    }

    private fun storeNotification(title: String, body: String) {
        // Here you can implement logic to store notifications locally or in Firestore
        // This depends on your app requirements
        val id = sharedPreferences.getString("id", null)
        if (!id.isNullOrEmpty()) {
            val notification = hashMapOf(
                "title" to title,
                "body" to body,
                "timestamp" to System.currentTimeMillis(),
                "read" to false
            )

            firestore.collection("users").document(id)
                .collection("notifications")
                .add(notification)
                .addOnSuccessListener {
                    Log.d(tag, "Notification stored successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(tag, "Failed to store notification", e)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(
            channelId,
            "Life AI Assistant Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications from Life AI Assistant"
            enableLights(true)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }
}