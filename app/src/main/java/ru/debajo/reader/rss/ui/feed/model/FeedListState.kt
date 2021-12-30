package ru.debajo.reader.rss.ui.feed.model

import ru.debajo.reader.rss.ui.article.model.UiArticle

data class FeedListState(
    val allArticles: List<UiArticle>,
    val showOnlyNewArticles: Boolean,
) {

    val articles: List<UiArticle>
        get() {
            return if (showOnlyNewArticlesButtonVisible) {
                allArticles.filter { !showOnlyNewArticles || it.isNew }
            } else {
                allArticles
            }
        }

    val showOnlyNewArticlesButtonVisible: Boolean
        get() = allArticles.any { it.isNew }
}
