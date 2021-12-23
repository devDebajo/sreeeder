package ru.debajo.reader.rss.data.updater

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.ext.ignoreElements
import ru.debajo.reader.rss.metrics.Analytics
import timber.log.Timber

class BackgroundUpdatesWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val workManager: WorkManager by inject()
    private val rssLoadDbManager: RssLoadDbManager by inject()
    private val articlesDao: ArticlesDao by inject()
    private val analytics: Analytics by inject()
    private val notificationManager: BackgroundUpdatesNotificationManager by inject()
    private val notificationChannelCreator: NotificationChannelCreator by inject()
    private val notificationFactory: NotificationFactory by inject()

    override suspend fun doWork(): Result {
        analytics.onStartWorker()
        setForeground(createForegroundInfo())
        return withContext(IO) {
            runCatching {
                rssLoadDbManager.refreshSubscriptions(true).ignoreElements()
                articlesDao.getUnreadArticlesCount()
            }
                .onSuccess { count ->
                    analytics.onSuccessWorker()
                    notificationManager.sendNewArticlesCount(count)
                }
                .onFailure { error ->
                    analytics.onFailWorker()
                    Timber.e(error)
                }
                .map { Result.success() }
                .getOrElse { Result.failure() }
        }
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
