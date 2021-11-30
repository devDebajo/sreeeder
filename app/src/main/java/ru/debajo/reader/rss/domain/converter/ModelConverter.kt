package ru.debajo.reader.rss.domain.converter

interface ModelConverter<T, R> {
    fun convert(item: T): R
}

fun <T, R> ModelConverter<T, R>.convertList(items: List<T>): List<R> {
    return items.map { convert(it) }
}
