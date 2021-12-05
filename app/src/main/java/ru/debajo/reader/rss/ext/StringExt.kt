package ru.debajo.reader.rss.ext

fun String.trimLastSlash(): String {
    return if (endsWith("/")) dropLast(1) else this
}