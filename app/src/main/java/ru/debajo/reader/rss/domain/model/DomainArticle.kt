package ru.debajo.reader.rss.domain.model

import org.joda.time.DateTime

data class DomainArticle(
    val id: String,
    val channelUrl: DomainChannelUrl,
    val channelName: String,
    val channelImage: String?,
    val author: String?,
    val title: String,
    val image: String?,
    val url: String,
    val bookmarked: Boolean,
    val contentHtml: String?,
    val timestamp: DateTime?,
    val categories: List<String>,
    val readPercents: Int,
)
