package ru.debajo.reader.rss.data.updater

import androidx.work.ListenableWorker
import androidx.work.WorkManager
import ru.debajo.reader.rss.data.error.WorkerScheduler
import ru.debajo.reader.rss.data.preferences.BackgroundUpdatesEnabledPreference
import java.util.concurrent.TimeUnit

class BackgroundUpdatesScheduler(
    workManager: WorkManager,
    private val backgroundUpdatesEnabledPreference: BackgroundUpdatesEnabledPreference,
) : WorkerScheduler(workManager) {

    override val tag: String = "BG_UPDATER_WORK_TAG"

    override val workerClass: Class<out ListenableWorker> = BackgroundUpdatesWorker::class.java

    override val repeatMs: Long = TimeUnit.HOURS.toMillis(4)

    override val initialDelayMs: Long = TimeUnit.HOURS.toMillis(1)

    override suspend fun isEnabled(): Boolean = backgroundUpdatesEnabledPreference.get()
}
