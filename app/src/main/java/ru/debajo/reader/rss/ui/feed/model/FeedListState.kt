package ru.debajo.reader.rss.ui.feed.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import ru.debajo.reader.rss.ui.article.ArticleOfflineStatus
import ru.debajo.reader.rss.ui.article.model.UiArticle

@Immutable
data class FeedListState(
    val allArticles: List<UiArticle> = emptyList(),
    val offlineStatuses: Map<String, ArticleOfflineStatus> = emptyMap(),
    val showOnlyNewArticles: Boolean = false,
) {
    private val elements: List<UiArticleElement> =
        allArticles.map { UiArticleElement.from(it, offlineStatuses) }

    val showOnlyNewArticlesButtonVisible: Boolean = elements.any { it.article.isNew }

    @Stable
    val articles: List<UiArticleElement> = if (showOnlyNewArticlesButtonVisible) {
        elements.filter { !showOnlyNewArticles || it.article.isNew }
    } else {
        elements
    }
}

@Immutable
data class UiArticleElement(
    val article: UiArticle,
    val offlineStatus: ArticleOfflineStatus,
) {
    fun updateArticle(block: UiArticle.() -> UiArticle): UiArticleElement {
        return copy(article = article.block())
    }

    companion object {
        fun from(article: UiArticle, map: Map<String, ArticleOfflineStatus>): UiArticleElement {
            var offlineStatus = map[article.id]
            if (offlineStatus == null) {
                offlineStatus = if (article.rawHtmlContent.isNullOrEmpty()) {
                    ArticleOfflineStatus.NOT_LOADED
                } else {
                    ArticleOfflineStatus.LOADED
                }
            }

            return UiArticleElement(article, offlineStatus)
        }
    }
}