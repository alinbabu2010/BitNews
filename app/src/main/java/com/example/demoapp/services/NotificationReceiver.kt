package com.example.demoapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.apply {
            val notificationId = getIntExtra("notificationId",0)
            context?.apply {
                NotificationManagerCompat.from(this).cancel(notificationId)
            }
        }
    }
}