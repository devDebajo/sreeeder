package ru.debajo.reader.rss.ui.article.parser

import androidx.compose.ui.graphics.Color

sealed interface WebPageTokenStyle {
    val start: Int
    val end: Int

    class Bold(override val start: Int, override val end: Int) : WebPageTokenStyle
    class Italic(override val start: Int, override val end: Int) : WebPageTokenStyle
    class BoldItalic(override val start: Int, override val end: Int) : WebPageTokenStyle
    class Underline(override val start: Int, override val end: Int) : WebPageTokenStyle
    class ForegroundColor(val color: Color, override val start: Int, override val end: Int) : WebPageTokenStyle
    class Url(val url: String, override val start: Int, override val end: Int) : WebPageTokenStyle
}
