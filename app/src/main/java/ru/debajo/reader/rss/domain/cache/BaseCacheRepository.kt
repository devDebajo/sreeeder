package ru.debajo.reader.rss.domain.cache

import org.koin.java.KoinJavaComponent

abstract class BaseCacheRepository {
    private val cacheManager: CacheManager by KoinJavaComponent.inject(CacheManager::class.java)

    abstract val durationMs: Long

    abstract val group: String

    suspend fun isCacheActual(dataId: String = group): Boolean {
        return cacheManager.isActual(group, dataId, durationMs)
    }

    suspend fun updateCacheKey(dataId: String = group) {
        cacheManager.saveMarker(group, dataId)
    }

    suspend fun splitActualIds(dataIds: List<String>): KeysSplit {
        val actual = cacheManager.filterActualIds(group, dataIds, durationMs)
        val nonActual = dataIds.filter { id -> id !in actual }
        return KeysSplit(actual.toList(), nonActual)
    }

    data class KeysSplit(
        val actualIds: List<String>,
        val nonActualIds: List<String>,
    )
}
