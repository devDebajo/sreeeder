package ru.debajo.reader.rss.domain.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel

class LoadArticlesUseCase(
    private val articlesRepository: ArticlesRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadBookmarked(): Flow<List<DomainArticle>> {
        return articleBookmarksRepository.observeArticles()
            .map { articles -> articles.toDomainList() }
            .flowOn(IO)
    }

    fun load(channel: DomainChannel): Flow<List<DomainArticle>> {
        return combine(
            articleBookmarksRepository.observeIds().map { it.toSet() },
            articlesRepository.getArticles(channel.url)
        ) { bookmarks, articles ->
            articles.map { article -> article.copy(bookmarked = article.id in bookmarks) }
        }
    }
}
