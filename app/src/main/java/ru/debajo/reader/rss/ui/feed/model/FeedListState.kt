package ru.debajo.reader.rss.ui.feed.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import ru.debajo.reader.rss.ui.article.model.UiArticle

@Immutable
data class FeedListState(
    val allArticles: List<UiArticle>,
    val showOnlyNewArticles: Boolean,
) {

    @Stable
    val articles: List<UiArticle> = if (showOnlyNewArticlesButtonVisible) {
        allArticles.filter { !showOnlyNewArticles || it.isNew }
    } else {
        allArticles
    }

    val showOnlyNewArticlesButtonVisible: Boolean
        get() = allArticles.any { it.isNew }
}
