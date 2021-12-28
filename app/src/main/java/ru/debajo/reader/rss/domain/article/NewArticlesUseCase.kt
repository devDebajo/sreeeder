package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debajo.reader.rss.ui.article.model.UiArticle

class NewArticlesUseCase(
    private val newArticlesRepository: NewArticlesRepository,
) {
    private val localIdsChanged: MutableStateFlow<Long> = MutableStateFlow(0)
    private val viewedArticlesIds: MutableSet<String> = HashSet()
    private val mutex: Mutex = Mutex()

    fun observeNewCount(): Flow<Int> {
        return combine(
            localIdsChanged,
            newArticlesRepository.observeIds(),
        ) { _, dbIds -> (dbIds - viewedArticlesIds).size }
    }

    suspend fun saveViewedArticles() {
        val ids = mutex.withLock {
            viewedArticlesIds.toList().also { viewedArticlesIds.clear() }
        }
        if (ids.isNotEmpty()) {
            newArticlesRepository.onViewed(ids)
            localIdsChanged.emit(System.currentTimeMillis())
        }
    }

    suspend fun onArticleViewed(article: UiArticle) {
        mutex.withLock {
            viewedArticlesIds.add(article.id)
        }
        localIdsChanged.emit(System.currentTimeMillis())
    }

    suspend fun getNewIds(): Set<String> {
        return newArticlesRepository.getNewArticlesIds() - getViewedArticlesIdsThreadSafe()
    }

    private suspend fun getViewedArticlesIdsThreadSafe(): Set<String> {
        return mutex.withLock { viewedArticlesIds.toSet() }
    }
}
