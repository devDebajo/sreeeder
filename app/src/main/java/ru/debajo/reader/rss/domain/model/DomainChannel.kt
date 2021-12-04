package ru.debajo.reader.rss.domain.model

data class DomainChannel(
    val url: DomainChannelUrl,
    val name: String,
    val image: String?,
    val description: String?,
)
