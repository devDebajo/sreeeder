package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.model.toDb
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.ui.article.ArticleOfflineStatus
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import ru.debajo.reader.rss.ui.article.parser.WebPageToken
import timber.log.Timber

// should be singleton
class ArticleOfflineContentUseCase(
    private val readableArticleHelper: ReadableArticleHelper,
    private val articlesRepository: ArticlesRepository,
    private val rssLoadDbManager: RssLoadDbManager,
) {

    fun observe(): Flow<Map<String, ArticleOfflineStatus>> {
        return flowOf(emptyMap())
    }

    suspend fun getWepPageTokens(article: DomainArticle): List<WebPageToken>? {
        if (!article.contentHtml.isNullOrEmpty()) {
            val tokens = prepareTokens(article.contentHtml)
            if (!tokens.isNullOrEmpty()) {
                return tokens
            }
        }

        val readableArticle = readableArticleHelper.loadReadableArticleHtml(article.url) ?: return null
        persist(article, readableArticle)
        return prepareTokens(readableArticle.html)
    }

    fun enqueuePreloading(article: DomainArticle) {
        
    }

    private suspend fun persist(
        article: DomainArticle,
        readableArticle: ReadableArticleHelper.ReadableArticle
    ) {
        var toPersist = article.copy(contentHtml = readableArticle.html)
        if (toPersist.image.isNullOrEmpty()) {
            toPersist = toPersist.copy(
                image = rssLoadDbManager.tryExtractImage(readableArticle.html)
            )
        }
        if (toPersist.title.isEmpty() && !readableArticle.title.isNullOrEmpty()) {
            toPersist = toPersist.copy(title = readableArticle.title)
        }

        articlesRepository.persist(toPersist.toDb())
    }

    private suspend fun prepareTokens(html: String): List<WebPageToken>? {
        return runCatching { withContext(Dispatchers.Default) { WebPageParser.parse(html) } }
            .onFailure { Timber.tag("ArticleOfflineContentUseCase").e(it) }
            .getOrNull()
    }
}
