package ru.debajo.reader.rss.ui.article.parser

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
sealed interface WebPageTokenStyle {
    val start: Int
    val end: Int

    data class Bold(override val start: Int, override val end: Int) : WebPageTokenStyle
    data class Italic(override val start: Int, override val end: Int) : WebPageTokenStyle
    data class BoldItalic(override val start: Int, override val end: Int) : WebPageTokenStyle
    data class Underline(override val start: Int, override val end: Int) : WebPageTokenStyle
    data class ForegroundColor(val color: Color, override val start: Int, override val end: Int) : WebPageTokenStyle
    data class Scale(val scale: Float, override val start: Int, override val end: Int) : WebPageTokenStyle
    data class Url(val url: String, override val start: Int, override val end: Int) : WebPageTokenStyle
}
