package com_2is.egypt.wipegadmin.ui

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WipEgAdminApp : Application() , Configuration.Provider{
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "fetchItemsId"
        const val UPLOAD_NOTIFICATION_CHANNEL_ID = "uploadRecordsId"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
   @SuppressLint("SuspiciousIndentation")
   private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Fetch Items",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService<NotificationManager>()
            manager?.createNotificationChannel(channel)
   val uploadNotificationChannel = NotificationChannel(
                UPLOAD_NOTIFICATION_CHANNEL_ID,
                "Upload Records",
                NotificationManager.IMPORTANCE_LOW
            )
            manager?.createNotificationChannel(uploadNotificationChannel)

        }
    }
}