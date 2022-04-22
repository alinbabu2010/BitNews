package com.example.demoapp.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.demoapp.R
import com.example.demoapp.api.Resource
import com.example.demoapp.database.ArticlesDatabase
import com.example.demoapp.models.Articles
import com.example.demoapp.receivers.NotificationReceiver
import com.example.demoapp.repository.ArticleRepository
import com.example.demoapp.ui.activities.dashboard.DashboardActivity
import com.example.demoapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Worker class for updating news data
 */
class NewsWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val articlesDAO = ArticlesDatabase.getDatabase(applicationContext).articlesDAO()
    private val repository = ArticleRepository(articlesDAO)

    override fun doWork(): Result {
        val articles = ArrayList<Articles>()
        try {
            repository.getArticles(1){ resource ->
                if(resource.status == Resource.Status.SUCCESS ) {
                    resource.data?.articles?.let { articles.addAll(it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.addArticles(articles)
                        notifyUser()
                    }
                }
            }
        } catch (e: Exception) {
            return Result.retry()
        }
       return Result.success()
    }

    /**
     * Method to notify user about news update
     */
    private fun notifyUser() {
        val intent = Intent(context, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val clearIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = Intent.ACTION_DELETE
            putExtra("notificationId", Constants.NOTIFICATION_ID)
        }
        val clearPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            clearIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(context, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(context.getString(R.string.channel_title))
            .setContentText(context.getString(R.string.channel_text))
            .setColor(Color.GREEN)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .addAction(
                R.drawable.quantum_ic_clear_grey600_24,
                context.getString(R.string.clear),
                clearPendingIntent
            )
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationBuilder.let {
            notificationManagerCompat.notify(
                Constants.NOTIFICATION_ID,
                it.build()
            )
        }
    }

    companion object {
        private const val EVENT_CHANNEL_ID = "EVENT_CHANNEL_ID"
        const val WORK_NAME = "com.example.demoapp.work.RefreshNewsDataWorker"
    }
}