package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel
import timber.log.Timber

class FeedListUseCase(
    private val subscriptionsUseCase: ChannelsSubscriptionsUseCase,
    private val loadArticlesUseCase: LoadArticlesUseCase,
) {

    operator fun invoke(): Flow<List<DomainArticle>> {
        return subscriptionsUseCase.observe()
            .flatMapLatest { subscribedChannels -> loadArticles(subscribedChannels) }
            .map { articles -> articles.sortedByDescending { it.timestamp?.millis } }
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    private fun loadArticles(channels: List<DomainChannel>): Flow<List<DomainArticle>> {
        if (channels.isEmpty()) {
            return flowOf(emptyList())
        }
        val flows = channels.map { channel ->
            loadArticlesUseCase.load(channel)
        }
        return combine(flows) { feeds: Array<List<DomainArticle>> ->
            feeds.flatMap { it }
        }
    }
}
