package ru.debajo.reader.rss.ui.article

import ru.debajo.reader.rss.ui.article.parser.WebPageToken

sealed interface UiArticleWebRenderState {
    val bookmarked: Boolean

    data class Loading(override val bookmarked: Boolean) : UiArticleWebRenderState
    data class Prepared(override val bookmarked: Boolean, val tokens: List<WebPageToken>) : UiArticleWebRenderState
    data class Error(override val bookmarked: Boolean) : UiArticleWebRenderState
}
