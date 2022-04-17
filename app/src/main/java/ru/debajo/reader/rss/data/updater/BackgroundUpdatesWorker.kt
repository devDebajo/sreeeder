package ru.debajo.reader.rss.data.updater

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import kotlinx.coroutines.runBlocking
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.domain.article.NewArticlesUseCase
import ru.debajo.reader.rss.ext.ignoreElements
import timber.log.Timber

class BackgroundUpdatesWorker(
    appContext: Context,
    params: WorkerParameters
) : Worker(appContext, params) {

    private val workManager: WorkManager by inject()
    private val rssLoadDbManager: RssLoadDbManager by inject()
    private val notificationManager: BackgroundUpdatesNotificationManager by inject()
    private val notificationChannelCreator: NotificationChannelCreator by inject()
    private val notificationFactory: NotificationFactory by inject()
    private val newArticlesUseCase: NewArticlesUseCase by inject()

    override fun doWork(): Result = runBlocking {
        runCatching {
            setForegroundAsync(createForegroundInfo()).await()
            rssLoadDbManager.refreshSubscriptions(true).ignoreElements()
            newArticlesUseCase.getNewIds().size
        }
            .onSuccess { count -> notificationManager.sendNewArticlesCount(count) }
            .onFailure { error -> Timber.e(error) }
            .map { Result.success() }
            .getOrElse { Result.failure() }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val cancel = applicationContext.getString(R.string.cancel)
        val text = applicationContext.getString(R.string.notification_updating)
        val intent = workManager.createCancelPendingIntent(id)

        notificationChannelCreator.updateOrCreate(SreeederNotificationChannel.ArticlesUpdate)

        val notification = notificationFactory.create(
            channel = SreeederNotificationChannel.ArticlesUpdate,
            text = text,
            ongoing = true,
            ticker = text,
            indeterminateProgress = true,
            actions = listOf(NotificationCompat.Action(android.R.drawable.ic_delete, cancel, intent))
        )

        return ForegroundInfo(NotificationIds.NEW_ARTICLES_UPDATING, notification)
    }
}
