package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel
import timber.log.Timber

class FeedListUseCase(
    private val subscriptionsUseCase: ChannelsSubscriptionsUseCase,
    private val loadArticlesUseCase: LoadArticlesUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
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
