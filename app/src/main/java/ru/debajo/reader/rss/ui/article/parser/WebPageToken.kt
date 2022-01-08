package ru.debajo.reader.rss.ui.article.parser

import androidx.compose.runtime.Stable

@Stable
sealed interface WebPageToken {
    val start: Int
    val end: Int

    operator fun contains(index: Int): Boolean = index in start..end

    data class Text(
        val text: String,
        val styles: List<WebPageTokenStyle> = emptyList(),
        val decoration: WebPageTokenDecoration? = null,
        override val start: Int = 0,
        override val end: Int = start + text.length
    ) : WebPageToken

    class Image(val url: String, override val start: Int) : WebPageToken {
        override val end: Int
            get() = start + 1
    }
}

fun WebPageToken.Text.obtainStyles(styles: List<WebPageTokenStyle>): WebPageToken.Text {
    val interestedStyles = mutableListOf<WebPageTokenStyle>()
    for (style in styles) {
        if (style.start >= start && style.end <= end) {
            interestedStyles.add(style)
        }
    }

    return copy(styles = interestedStyles)
}
