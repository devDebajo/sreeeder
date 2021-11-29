package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
class FeedListUseCase(
    private val subscriptionsUseCase: ChannelsSubscriptionsUseCase,
    private val channelsRepository: ChannelsRepository,
) {

    operator fun invoke(): Flow<List<FeedDomainArticle>> {
        return subscriptionsUseCase.observe()
            .flatMapLatest { subscribedChannels -> loadArticles(subscribedChannels) }
            .map { articles -> articles.sortedByDescending { it.article.timestamp?.millis } }
            .catch {
                Timber.e(it)
                emit(emptyList())
            }
    }

    private fun loadArticles(channels: List<DomainChannel>): Flow<List<FeedDomainArticle>> {
        val flows = channels.map { channel ->
            channelsRepository.getArticles(channel.url).map { articles ->
                articles.map { article -> FeedDomainArticle(article, channel) }
            }
        }
        return combine(flows) { feeds -> feeds.flatMap { it } }
    }

    class FeedDomainArticle(
        val article: DomainArticle,
        val channel: DomainChannel,
    )
}
