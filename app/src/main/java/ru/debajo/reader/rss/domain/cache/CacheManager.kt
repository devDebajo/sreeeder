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

    suspend fun saveMarker(group: String, dataId: String) {
        val marker = DbCacheMarker(group, dataId, nowUtcMillis)
        dao.insert(marker)
    }

    suspend fun isActual(group: String, dataId: String, durationMs: Long): Boolean {
        if (!BuildConfig.CACHE_ENABLED) {
            return false
        }
        val marker = dao.get(group, dataId) ?: return false
        return nowUtcMillis - marker.timestamp < durationMs
    }

    suspend fun filterActualIds(group: String, dataIds: List<String>, durationMs: Long): Set<String> {
        if (!BuildConfig.CACHE_ENABLED) {
            return emptySet()
        }
        val now = nowUtcMillis
        return dao.get(group, dataIds)
            .filter { now - it.timestamp < durationMs }
            .map { it.dataId }
            .toSet()
    }
}
