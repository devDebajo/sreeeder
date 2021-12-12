package ru.debajo.reader.rss.ui.feed.model

import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.list.UiListItem

data class UiArticleListItem(val article: UiArticle) : UiListItem {
    override val id: String
        get() = article.id + article.channelName
}
