package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.model.DomainChannel
import timber.log.Timber

class FeedListUseCase(
    private val subscriptionsUseCase: ChannelsSubscriptionsUseCase,
    private val loadArticlesUseCase: LoadArticlesUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<LoadArticlesUseCase.EnrichedDomainArticle>> {
        return subscriptionsUseCase.observe()
            .flatMapLatest { subscribedChannels -> loadArticles(subscribedChannels) }
            .map { articles ->
                throw IllegalStateException()
                articles.sortedByDescending { it.article.timestamp?.millis }
            }
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    private fun loadArticles(channels: List<DomainChannel>): Flow<List<LoadArticlesUseCase.EnrichedDomainArticle>> {
        if (channels.isEmpty()) {
            return flowOf(emptyList())
        }
        val flows = channels.map { channel ->
            loadArticlesUseCase.load(channel)
        }
        return combine(flows) { feeds: Array<List<LoadArticlesUseCase.EnrichedDomainArticle>> ->
            feeds.flatMap { it }
        }
    }
}
