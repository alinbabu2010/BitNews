package com.example.demoapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SpeechNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.sendBroadcast(Intent("action").putExtra("actionName", intent?.action))
    }
}