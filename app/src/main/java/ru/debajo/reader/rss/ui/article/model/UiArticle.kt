package ru.debajo.reader.rss.ui.article.model

import org.joda.time.DateTime
import org.jsoup.nodes.Document

data class UiArticle(
    val id: String,
    val author: String,
    val title: String,
    val description: Document,
    val content: Document,
    val timestamp: DateTime
)
