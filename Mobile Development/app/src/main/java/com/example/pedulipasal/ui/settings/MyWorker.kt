package com.example.pedulipasal.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pedulipasal.R
import com.example.pedulipasal.data.NewsRepository
import com.example.pedulipasal.di.Injection
import com.example.pedulipasal.page.detail.DetailNewsActivity
import kotlinx.coroutines.runBlocking

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val newsRepository: NewsRepository = Injection.provideNewsRepository(context)

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }

    private var resultStatus: Result? = null

    override fun doWork(): Result {
        return getDailyNews()
    }

    fun getDailyNews(): Result {
        try {
            val data = runBlocking {
                newsRepository.getDailyNews()
            }
            showNotification(data.title ?: "empty title", data.description ?: "empty description", data.url ?: "empty link")
            resultStatus = Result.success()
        } catch (e: Exception) {
            resultStatus = Result.failure()
        }
        return resultStatus as Result
    }

    fun showNotification(title: String, description: String, link: String) {
        //val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val intent = Intent(applicationContext, DetailNewsActivity::class.java)
        //Log.d("MyWorker", link)
        intent.putExtra("WEB_URL", link)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}