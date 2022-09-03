package ru.debajo.reader.rss.ui.feed.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import ru.debajo.reader.rss.ui.article.ArticleOfflineStatus
import ru.debajo.reader.rss.ui.article.model.UiArticle

@Immutable
data class FeedListState(
    val allArticles: List<UiArticle> = emptyList(),
    val loadingIds: Set<String> = emptySet(),
    val showOnlyNewArticles: Boolean = false,
) {
    private val elements: List<UiArticleElement> =
        allArticles.map { UiArticleElement.from(it, loadingIds) }

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
        fun from(article: UiArticle, loadingIds: Set<String>): UiArticleElement {
            val offlineStatus = if (article.id in loadingIds) {
                ArticleOfflineStatus.LOADING
            } else if (article.rawHtmlContent.isNullOrEmpty()) {
                ArticleOfflineStatus.NOT_LOADED
            } else {
                ArticleOfflineStatus.LOADED
            }

            return UiArticleElement(article, offlineStatus)
        }
    }
}