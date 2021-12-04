package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel

class LoadArticlesUseCase(
    private val channelsRepository: ChannelsRepository,
    private val articlesRepository: ArticlesRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadBookmarked(): Flow<List<EnrichedDomainArticle>> {
        return articleBookmarksRepository.observe()
            .flatMapLatest { ids -> articlesRepository.getArticles(ids) }
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

    fun load(channel: DomainChannel): Flow<List<EnrichedDomainArticle>> {
        return combine(
            articleBookmarksRepository.observe().map { it.toSet() },
            articlesRepository.getArticles(channel.url)
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
