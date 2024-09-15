package com_2is.egypt.wipegadmin.ui.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com_2is.egypt.wipegadmin.R
import com_2is.egypt.wipegadmin.domain.gateways.FETCH_PROGRESS_TYPE_EXTRA
import com_2is.egypt.wipegadmin.domain.gateways.FETCH_PROGRESS_VALUE_EXTRA
import com_2is.egypt.wipegadmin.ui.WipEgAdminApp
import com_2is.egypt.wipegadmin.ui.features.MainActivity

class ForegroundFetchService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val progressType = intent!!.getStringExtra(FETCH_PROGRESS_TYPE_EXTRA)!!
        val progress = intent.getIntExtra(FETCH_PROGRESS_VALUE_EXTRA, 0)
        startForeground(1, createNotification(progressType = progressType, progress = progress))
        if (progress == 100)
            stopForeground(false)
        return START_NOT_STICKY


    }

    private fun createNotification(progressType: String, progress: Int): Notification {
        val intent = Intent(this@ForegroundFetchService, MainActivity::class.java)
            .run {
                PendingIntent.getActivity(this@ForegroundFetchService, 0, this, 0)
            }
        return NotificationCompat.Builder(this, WipEgAdminApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Fetching Items")
            .setContentText(progressType)
            .setSmallIcon(R.mipmap.ic_launcher)
            .let {
                if (progress != 100)
                    it.setProgress(100, progress, false) else it

            }.setAutoCancel(progress == 100)
            .setContentIntent(intent)
            .build()

    }
}