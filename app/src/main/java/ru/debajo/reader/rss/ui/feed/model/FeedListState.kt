package ru.debajo.reader.rss.ui.feed.model

import ru.debajo.reader.rss.ui.article.model.UiArticle

data class FeedListState(
    val articles: List<UiArticle>,
)
