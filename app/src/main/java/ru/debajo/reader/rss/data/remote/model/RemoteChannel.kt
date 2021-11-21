package ru.debajo.reader.rss.data.remote.model

class RemoteChannel(
    val url: String,
    val name: String,
    val description: String?,
    val currentArticles: List<RemoteArticle>
)
