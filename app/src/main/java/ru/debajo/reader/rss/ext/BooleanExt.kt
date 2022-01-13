package ru.debajo.reader.rss.ext

fun Boolean.toInt(positive: Int = 1, negative: Int = 0): Int {
    return if (this) positive else negative
}