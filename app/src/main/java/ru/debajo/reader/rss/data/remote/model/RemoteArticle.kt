package ru.debajo.reader.rss.data.remote.model

import org.joda.time.DateTime

class RemoteArticle(
    val author: String?,
    val id: String?,
    val title: String?,
    val descriptionHtml: String?,
    val contentHtml: String?,
    val timestamp: DateTime?
)
