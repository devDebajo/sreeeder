package ru.debajo.reader.rss.domain.model

import org.joda.time.DateTime

data class DomainArticle(
    val id: String,
    val channelUrl: DomainChannelUrl,
    val author: String?,
    val title: String,
    val image: String?,
    val url: String,
    val bookmarked: Boolean,
    val contentHtml: String?,
    val timestamp: DateTime?
)

