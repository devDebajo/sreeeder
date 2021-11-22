package ru.debajo.reader.rss.domain.model

data class DomainChannel(
    val url: String,
    val name: String,
    val image: String?,
    val description: String?,
)
