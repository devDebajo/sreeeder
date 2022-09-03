package ru.debajo.reader.rss.domain.article

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.model.toDb
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import ru.debajo.reader.rss.ui.article.parser.WebPageToken
import timber.log.Timber

// should be singleton
class ArticleOfflineContentUseCase(
    private val readableArticleHelper: ReadableArticleHelper,
    private val articlesRepository: ArticlesRepository,
    private val rssLoadDbManager: RssLoadDbManager,
) {

    private val scope: LifecycleCoroutineScope = ProcessLifecycleOwner.get().lifecycle.coroutineScope

    private val loadingArticles: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())

    fun observeLoadingIds(): Flow<Set<String>> = loadingArticles.asStateFlow()

    suspend fun getWepPageTokens(article: DomainArticle): List<WebPageToken>? {
        if (!article.contentHtml.isNullOrEmpty()) {
            val tokens = prepareTokens(article.contentHtml)
            if (!tokens.isNullOrEmpty()) {
                return tokens
            }
        }

        notifyLoading(article.id, true)
        val readableArticle = readableArticleHelper.loadReadableArticleHtml(article.url)
        notifyLoading(article.id, false)
        if (readableArticle == null) {
            return null
        }

        persist(article, readableArticle)
        return prepareTokens(readableArticle.html)
    }

    fun enqueuePreloading(article: DomainArticle) {
        if (article.id in loadingArticles.value) {
            return
        }

        scope.launch(IO) {
            supervisorScope {
                notifyLoading(article.id, true)
                val readableArticle = readableArticleHelper.loadReadableArticleHtml(article.url)
                notifyLoading(article.id, false)
                if (readableArticle != null) {
                    persist(article, readableArticle)
                }
            }
        }
    }

    private fun notifyLoading(id: String, loading: Boolean) {
        val loadingArticles = loadingArticles.value.toMutableSet()
        if (loading) {
            loadingArticles.add(id)
        } else {
            loadingArticles.remove(id)
        }
        this.loadingArticles.value = loadingArticles
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
