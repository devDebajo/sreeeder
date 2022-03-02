package ru.debajo.reader.rss.data.error

import androidx.work.ListenableWorker
import androidx.work.WorkManager
import ru.debajo.reader.rss.data.preferences.CrashlyticsPreference
import java.util.concurrent.TimeUnit

class SendErrorsScheduler(
    workManager: WorkManager,
    private val crashlyticsPreference: CrashlyticsPreference,
) : WorkerScheduler(workManager) {

    override val tag: String = "SEND_ERRORS_WORK_TAG"

    override val workerClass: Class<out ListenableWorker> = SendErrorsWorker::class.java

    override val repeatMs: Long = TimeUnit.HOURS.toMillis(1)

    override val initialDelayMs: Long = TimeUnit.SECONDS.toMillis(10)

    override suspend fun isEnabled(): Boolean = crashlyticsPreference.get()
}
