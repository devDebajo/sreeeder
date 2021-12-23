package ru.debajo.reader.rss.data.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationChannelCreator(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun updateOrCreate(channel: SreeederNotificationChannel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val systemChannel = NotificationChannel(
                channel.id,
                context.getString(channel.name),
                importance
            ).apply {
                this.description = context.getString(channel.description)
            }
            notificationManager.createNotificationChannel(systemChannel)
        }
    }
}
