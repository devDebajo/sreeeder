package ru.debajo.reader.rss.data.updater

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import ru.debajo.reader.rss.data.preferences.BackgroundUpdatesEnabledPreference
import java.util.concurrent.TimeUnit

class BackgroundUpdatesScheduler(
    private val workManager: WorkManager,
    private val backgroundUpdatesEnabledPreference: BackgroundUpdatesEnabledPreference,
) {
    suspend fun rescheduleOrCancel() {
        if (backgroundUpdatesEnabledPreference.get()) {
            reschedule()
        } else {
            removeWorks()
        }
    }

    fun reschedule() {
        workManager.enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            createRequest()
        )
    }

    fun removeWorks() {
        workManager.cancelUniqueWork(WORK_TAG)
    }

    private fun createRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            BackgroundUpdatesWorker::class.java,
            4,
            TimeUnit.HOURS
        ).build()
    }

    private companion object {
        const val WORK_TAG = "WORK_TAG"
    }
}
