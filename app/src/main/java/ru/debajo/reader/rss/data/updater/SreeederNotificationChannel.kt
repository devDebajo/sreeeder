package ru.debajo.reader.rss.data.updater

import ru.debajo.reader.rss.R

sealed interface SreeederNotificationChannel {
    val id: String
    val name: Int
    val description: Int

    object ArticlesUpdate : SreeederNotificationChannel {
        override val id: String = "NewArticlesChannel"
        override val name: Int = R.string.notification_channel_name
        override val description: Int = R.string.notification_channel_description
    }
}
