package ru.debajo.reader.rss.ui.article.parser

sealed interface WebPageTokenDecoration {
    object Bullet : WebPageTokenDecoration
    object Quote : WebPageTokenDecoration
}
