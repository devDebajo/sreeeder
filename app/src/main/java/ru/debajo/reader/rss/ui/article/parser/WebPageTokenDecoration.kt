package ru.debajo.reader.rss.ui.article.parser

import androidx.compose.runtime.Stable

@Stable
sealed interface WebPageTokenDecoration {
    object Bullet : WebPageTokenDecoration
    object Quote : WebPageTokenDecoration
}
