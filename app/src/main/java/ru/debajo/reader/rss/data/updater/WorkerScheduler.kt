package ru.debajo.reader.rss.data.updater

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

abstract class WorkerScheduler(
    private val workManager: WorkManager,
) {
    abstract val tag: String

    abstract val workerClass: Class<out ListenableWorker>

    abstract val repeatMs: Long

    abstract val initialDelayMs: Long

    abstract suspend fun isEnabled(): Boolean

    suspend fun rescheduleOrCancel() {
        if (isEnabled()) {
            reschedule()
        } else {
            removeWorks()
        }
    }

    fun reschedule() {
        workManager.enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.REPLACE,
            createRequest()
        )
    }

    fun removeWorks() {
        workManager.cancelUniqueWork(tag)
    }

    open fun createRequest(): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(workerClass, repeatMs, TimeUnit.MILLISECONDS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()
    }
}
