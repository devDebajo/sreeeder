package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.model.DomainChannel
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
class FeedListUseCase(
    private val subscriptionsUseCase: ChannelsSubscriptionsUseCase,
    private val loadArticlesUseCase: LoadArticlesUseCase,
) {

    operator fun invoke(): Flow<List<LoadArticlesUseCase.EnrichedDomainArticle>> {
        return subscriptionsUseCase.observe()
            .flatMapLatest { subscribedChannels -> loadArticles(subscribedChannels) }
            .map { articles -> articles.sortedByDescending { it.article.timestamp?.millis } }
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    private fun loadArticles(channels: List<DomainChannel>): Flow<List<LoadArticlesUseCase.EnrichedDomainArticle>> {
        val flows = channels.map { channel -> loadArticlesUseCase.load(channel, true) }
        return combine(flows) { feeds: Array<List<LoadArticlesUseCase.EnrichedDomainArticle>> -> feeds.flatMap { it } }
    }
}
