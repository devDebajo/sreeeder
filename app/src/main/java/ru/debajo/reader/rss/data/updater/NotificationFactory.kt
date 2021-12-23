package ru.debajo.reader.rss.data.updater

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import ru.debajo.reader.rss.R

class NotificationFactory(private val context: Context) {
    fun create(
        channel: SreeederNotificationChannel,
        text: String,
        ticker: String? = null,
        ongoing: Boolean = false,
        autoCancel: Boolean = false,
        pendingIntent: PendingIntent? = null,
        indeterminateProgress: Boolean = false,
        actions: List<NotificationCompat.Action> = emptyList(),
    ): Notification {
        return NotificationCompat.Builder(context, channel.id)
            .setSmallIcon(R.drawable.ic_rss_feed)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .apply {
                if (pendingIntent != null) {
                    setContentIntent(pendingIntent)
                }
                if (ticker != null) {
                    setTicker(ticker)
                }
                for (action in actions) {
                    addAction(action)
                }
                if (indeterminateProgress) {
                    setProgress(0, 0, true)
                }
            }
            .setOngoing(ongoing)
            .setAutoCancel(autoCancel)
            .build()
    }
}
