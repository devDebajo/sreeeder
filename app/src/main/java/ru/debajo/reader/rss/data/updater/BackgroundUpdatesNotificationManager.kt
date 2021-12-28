package ru.debajo.reader.rss.data.updater

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.host.HostActivity

class BackgroundUpdatesNotificationManager(
    private val context: Context,
    private val notificationFactory: NotificationFactory,
    private val notificationChannelCreator: NotificationChannelCreator,
    private val notificationManager: NotificationManager
) {

    fun sendNewArticlesCount(count: Int) {
        if (count == 0) {
            cancel()
            return
        }

        notificationChannelCreator.updateOrCreate(SreeederNotificationChannel.ArticlesUpdate)

        val notification = notificationFactory.create(
            channel = SreeederNotificationChannel.ArticlesUpdate,
            text = context.getString(R.string.notification_text, count),
            autoCancel = true,
            pendingIntent = createPendingIntent()
        )

        notificationManager.notify(NOTIFICATION_ID, notification)
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

    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private companion object {
        const val NOTIFICATION_ID = NotificationIds.NEW_ARTICLES
        const val NOTIFICATION_TAP_REQUEST_CODE = 12
    }
}
