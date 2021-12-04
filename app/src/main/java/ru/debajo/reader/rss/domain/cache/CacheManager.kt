package ru.debajo.reader.rss.domain.cache

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.reader.rss.data.db.dao.CacheMarkerDao
import ru.debajo.reader.rss.data.db.model.DbCacheMarker

class CacheManager(
    private val dao: CacheMarkerDao,
) {
    private val nowUtcMillis: Long
        get() = DateTime.now(DateTimeZone.UTC).millis

    suspend fun saveMarker(key: String) {
        val marker = DbCacheMarker(key, nowUtcMillis)
        dao.insert(marker)
    }

    suspend fun isActual(key: String, durationMs: Long): Boolean {
        if (!BuildConfig.CACHE_ENABLED) {
            return false
        }
        val marker = dao.get(key) ?: return false
        return nowUtcMillis - marker.timestamp < durationMs
    }
}
