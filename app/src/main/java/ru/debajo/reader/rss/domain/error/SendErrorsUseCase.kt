package ru.debajo.reader.rss.domain.error

import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.converter.toRemote
import ru.debajo.reader.rss.data.error.ErrorRepository
import ru.debajo.reader.rss.data.preferences.CrashlyticsPreference
import ru.debajo.reader.rss.data.preferences.base.runIfTrue
import ru.debajo.reader.rss.data.remote.service.ErrorService

class SendErrorsUseCase(
    private val errorRepository: ErrorRepository,
    private val errorService: ErrorService,
    private val crashlyticsPreference: CrashlyticsPreference,
) {
    suspend fun record(
        throwable: Throwable,
        fatal: Boolean,
        customMessage: String? = null,
        tag: String? = null,
    ) = crashlyticsPreference.runIfTrue {
        errorRepository.insert(throwable, fatal, customMessage, tag)
    }

    suspend fun sendAllPending() = crashlyticsPreference.runIfTrue {
        val pendingErrors = runCatching { errorRepository.getAll() }
            .onFailure { sendImmediately(it, customMessage = "Error while loading pending errors from Db") }
            .getOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?: return@runIfTrue

        runCatching { errorService.send(pendingErrors.map { it.toRemote() }) }
            .onSuccess { errorRepository.clear() }
    }

    private suspend fun sendImmediately(
        throwable: Throwable,
        fatal: Boolean = false,
        customMessage: String? = null,
        tag: String? = null,
    ) = crashlyticsPreference.runIfTrue {
        runCatching {
            errorService.send(listOf(throwable.toDb(fatal, customMessage, tag).toRemote()))
        }
    }
}
