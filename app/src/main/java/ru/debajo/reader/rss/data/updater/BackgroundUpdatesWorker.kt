package ru.debajo.reader.rss.data.updater

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
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

    private val rssLoadDbManager: RssLoadDbManager by inject()
    private val articlesDao: ArticlesDao by inject()
    private val analytics: Analytics by inject()
    private val notificationManager: BackgroundUpdatesNotificationManager by inject()

    override suspend fun doWork(): Result {
        analytics.onStartWorker()
        return withContext(IO) {
            runCatching {
                rssLoadDbManager.refreshSubscriptions(true).ignoreElements()
                articlesDao.getUnreadArticlesCount()
            }
                .onSuccess { count ->
                    analytics.onSuccessWorker()
                    notificationManager.send(count)
                }
                .onFailure { error ->
                    analytics.onFailWorker()
                    Timber.e(error)
                }
                .map { Result.success() }
                .getOrElse { Result.failure() }
        }
    }
}
