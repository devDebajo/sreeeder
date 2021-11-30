package ru.debajo.reader.rss.domain.model

import org.joda.time.DateTime

data class DomainArticle(
    val id: String,
    val author: String?,
    val title: String,
    val image: String?,
    val url: String,
    val contentHtml: String?,
    val timestamp: DateTime?
)

