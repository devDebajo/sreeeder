package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.channel.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel

@FlowPreview
@ExperimentalCoroutinesApi
class LoadArticlesUseCase(
    private val channelsRepository: ChannelsRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) {

    fun loadBookmarked(): Flow<List<EnrichedDomainArticle>> {
        return articleBookmarksRepository.observe()
            .map { ids -> channelsRepository.getArticles(ids) }
            .flatMapLatest { articles ->
                channelsRepository.getChannelsByUrls(articles.map { it.channelUrl }.toSet().toList())
                    .map { channels ->
                        val channelsMap = channels.associateBy { channel -> channel.url }
                        articles.map { article ->
                            EnrichedDomainArticle(
                                article = article.copy(bookmarked = true),
                                channel = channelsMap[article.channelUrl],
                            )
                        }
                    }
            }
            .flowOn(IO)
    }

    fun load(channel: DomainChannel, force: Boolean = false): Flow<List<EnrichedDomainArticle>> {
        return combine(
            articleBookmarksRepository.observe().map { it.toSet() },
            channelsRepository.getArticles(channel.url, force)
        ) { bookmarks, articles ->
            articles.map { article ->
                EnrichedDomainArticle(
                    article = article.copy(bookmarked = article.id in bookmarks),
                    channel = channel,
                )
            }
        }
    }

    class EnrichedDomainArticle(
        val article: DomainArticle,
        val channel: DomainChannel?,
    )
}
