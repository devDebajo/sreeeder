package ru.debajo.reader.rss.data.remote.model

data class RemoteChannel(
    val url: String,
    val name: String,
    val description: String?,
    val image: String?,
    val currentArticles: List<RemoteArticle>
)
