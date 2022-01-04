package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.ui.article.model.UiArticle

@OptIn(ExperimentalCoroutinesApi::class)
class NewArticlesUseCase(
    private val newArticlesRepository: NewArticlesRepository,
    private val channelsSubscriptionsUseCase: ChannelsSubscriptionsUseCase,
) {
    private val localIdsChanged: MutableStateFlow<Long> = MutableStateFlow(0)
    private val viewedArticlesIds: MutableSet<String> = HashSet()
    private val mutex: Mutex = Mutex()

    fun observeNewCount(): Flow<Int> {
        return channelsSubscriptionsUseCase.observe().flatMapLatest {
            combine(
                localIdsChanged,
                newArticlesRepository.observeIds(),
            ) { _, dbIds -> (dbIds - viewedArticlesIds).size }
        }
    }

    suspend fun saveViewedArticles() {
        val ids = mutex.withLock {
            viewedArticlesIds.toList().also { viewedArticlesIds.clear() }
        }
        if (ids.isNotEmpty()) {
            newArticlesRepository.onViewed(ids)
            localIdsChanged.notify()
        }
    }

    suspend fun onArticleViewed(article: UiArticle) {
        mutex.lock()
        if (article.id in viewedArticlesIds) {
            mutex.unlock()
            return
        }
        viewedArticlesIds.add(article.id)
        mutex.unlock()
        localIdsChanged.notify()
    }

    suspend fun getNewIds(): Set<String> {
        return newArticlesRepository.getNewArticlesIds() - getViewedArticlesIdsThreadSafe()
    }

    suspend fun markAllAsRead() {
        mutex.withLock { viewedArticlesIds.clear() }
        newArticlesRepository.removeAll()
        localIdsChanged.notify()
    }

    private suspend fun MutableStateFlow<Long>.notify() {
        emit(System.currentTimeMillis())
    }

    private suspend fun getViewedArticlesIdsThreadSafe(): Set<String> {
        return mutex.withLock { viewedArticlesIds.toSet() }
    }
}
