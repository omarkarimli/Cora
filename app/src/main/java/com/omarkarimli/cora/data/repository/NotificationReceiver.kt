package com.omarkarimli.cora.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.repository.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationRepo: NotificationRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                notificationRepo.scheduleDailyNotifications()
            }
            ACTION_SHOW_NOTIFICATION -> {
                showNotification(context)
            }
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "voux_channel",
                "Voux Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "voux_channel")
            .setContentTitle("Voux")
            .setContentText(context.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.app_icon_with_bg)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.vouxglobal.voux.notification.SHOW_NOTIFICATION"
    }
}
