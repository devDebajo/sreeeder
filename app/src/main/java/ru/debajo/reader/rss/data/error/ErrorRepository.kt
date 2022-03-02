package ru.debajo.reader.rss.data.error

import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.db.dao.ErrorsDao
import ru.debajo.reader.rss.data.db.model.DbError

class ErrorRepository(
    private val dao: ErrorsDao,
) {
    suspend fun getAll(): List<DbError> = dao.getAll()

    suspend fun insert(
        throwable: Throwable,
        fatal: Boolean,
        customMessage: String?,
        tag: String?
    ) {
        dao.insert(throwable.toDb(fatal, customMessage, tag))
    }

    suspend fun clear() {
        dao.deleteAll()
    }
}
