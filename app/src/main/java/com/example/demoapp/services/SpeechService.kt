package com.example.demoapp.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.demoapp.R
import com.example.demoapp.receivers.SpeechNotificationReceiver
import com.example.demoapp.utils.Constants.Companion.ARTICLE
import com.example.demoapp.utils.Constants.Companion.NOTIFICATION_ID
import java.util.*

/**
 *  This is the service class for text to speech running on background.
 */
class SpeechService : Service() {

    private var textToSpeech: TextToSpeech? = null
    private var notification: Notification? = null
    var contentToSpeech = ""

    override fun onCreate() {
        super.onCreate()
        registerReceiver(broadcastReceiver, IntentFilter(FILTER_NAME))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        contentToSpeech = intent?.getStringExtra(ARTICLE).toString()
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.ENGLISH
                setUpNotification(ACTION_PAUSE, R.drawable.ic_pause)
                textToSpeech?.speak(contentToSpeech, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.i(TAG, "onStart: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        stopService(intent)
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "onError: $utteranceId")
                    }
                })
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        textToSpeech?.shutdown()
    }

    /**
     * Method to show reading notification and control
     * @param action String value representing the action
     * @param icon Resource drawable id
     */
    private fun setUpNotification(action: String, icon: Int) {
        var pendingIntentStop: PendingIntent? = null
        var pendingIntentPauseOrPlay: PendingIntent? = null
        createChannel()
        pendingIntentStop = Intent(this, SpeechNotificationReceiver::class.java).let {
            it.action = ACTION_STOP
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        pendingIntentPauseOrPlay = Intent(this, SpeechNotificationReceiver::class.java).let {
            it.action = action
            PendingIntent.getBroadcast(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(getString(R.string.service_channel_title))
            .setColor(Color.RED)
            .setAutoCancel(true)
            .setNotificationSilent()
            .addAction(icon, "Pause", pendingIntentPauseOrPlay)
            .addAction(R.drawable.ic_stop, "Stop", pendingIntentStop)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Method to create notification channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channelName = getString(R.string.channel_name)
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.channel_description)
        channel.shouldShowLights()
        val notificationManager =
            ContextCompat.getSystemService(this, NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.extras?.getString(FILTER_EXTRAS_KEY)) {
                ACTION_STOP -> {
                    val speechIntent = Intent(applicationContext, SpeechService::class.java)
                    stopService(speechIntent)
                }
                ACTION_PAUSE -> {
                    textToSpeech?.stop()
                    setUpNotification(ACTION_PLAY, R.drawable.ic_play)
                }
                ACTION_PLAY -> {
                    textToSpeech?.speak(contentToSpeech, TextToSpeech.QUEUE_FLUSH, null, TTS_ID)
                    setUpNotification(ACTION_PAUSE, R.drawable.ic_pause)
                }
            }
        }
    }


    companion object {
        private const val TAG = "SpeechService"
        private const val CHANNEL_ID = "SERVICE_CHANNEL_ID"
        private const val ACTION_STOP = "actionStop"
        private const val ACTION_PLAY = "actionPlay"
        private const val ACTION_PAUSE = "actionPause"
        private const val TTS_ID = "Article Content"
        private const val FILTER_NAME = "action"
        private const val FILTER_EXTRAS_KEY = "actionName"
    }
}
