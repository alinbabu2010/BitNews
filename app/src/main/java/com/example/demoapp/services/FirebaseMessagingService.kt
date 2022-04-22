package com.example.demoapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.demoapp.R
import com.example.demoapp.models.Articles
import com.example.demoapp.ui.activities.dashboard.ArticleActivity
import com.example.demoapp.utils.Constants.Companion.ARTICLE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import kotlin.random.Random

/**
 * Service class for firebase messaging service
 */
class FirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManager

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        var pendingIntent: PendingIntent?
        p0.let { message ->
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //Setting up Notification channels for android O and above
            setupNotificationChannels()
            val intent = Intent(applicationContext, ArticleActivity::class.java)
            val article = message.data["articles"]
            val gson = GsonBuilder().setPrettyPrinting().create()
            val articles = gson.fromJson(article, Articles::class.java)
            println(articles)
            intent.putExtra(ARTICLE, articles)
            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val notificationId = Random.nextInt(60000)
            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["message"])
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    /**
     * Method to setup notification channel for firebase push notification
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val adminChannelName = getString(R.string.push_notification_channel_id)
        val adminChannelDescription = getString(R.string.push_notification_channel_description)

        val adminChannel =
            NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }

    companion object {
        private const val CHANNEL_ID = "Firebase"
        private const val TAG = "MyFirebaseToken"
    }
}