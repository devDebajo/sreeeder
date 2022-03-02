package ru.debajo.reader.rss.data.error

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.domain.error.SendErrorsUseCase

class SendErrorsWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val sendErrorsUseCase: SendErrorsUseCase by inject()

    override suspend fun doWork(): Result {
        sendErrorsUseCase.sendAllPending()
        return Result.success()
    }
}
