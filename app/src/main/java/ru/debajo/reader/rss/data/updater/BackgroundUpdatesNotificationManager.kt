package ru.debajo.reader.rss.data.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.host.HostActivity

class BackgroundUpdatesNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun send(count: Long) {
        if (count == 0L) {
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }

        createChannel()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_rss_feed)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_text, count))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = HostActivity.createIntent(context)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(context, NOTIFICATION_TAP_REQUEST_CODE, intent, flags)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private companion object {
        const val CHANNEL_ID = "NewArticlesChannel"
        const val NOTIFICATION_ID = 12332131
        const val NOTIFICATION_TAP_REQUEST_CODE = 12
    }
}
