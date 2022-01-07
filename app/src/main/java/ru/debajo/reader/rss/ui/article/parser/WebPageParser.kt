package ru.debajo.reader.rss.ui.article.parser

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.*
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

object WebPageParser {
    fun parse(html: String): List<WebPageToken> {
        val spannable = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS)
        return spannable.prepare()
    }

    private fun Spanned.prepare(): List<WebPageToken> {
        val pageAsString = toString()
        val spans = getSpans<Any>()
        val result = mutableListOf<WebPageToken>(WebPageToken.Text(pageAsString))
        for (span in spans) {
            val spanStart = getSpanStart(span)
            val spanEnd = getSpanEnd(span)
            when (span) {
                is ImageSpan -> {
                    if (span.source != null) {
                        result.insertImage(spanStart, span.source!!)
                    }
                }

                is URLSpan -> {
                    if (span.url != null) {
                        result.insertStyle(WebPageTokenStyle.Url(span.url!!, spanStart, spanEnd))
                    }
                }

                is StyleSpan -> {
                    when (span.style) {
                        Typeface.BOLD -> result.insertStyle(WebPageTokenStyle.Bold(spanStart, spanEnd))
                        Typeface.ITALIC -> result.insertStyle(WebPageTokenStyle.Italic(spanStart, spanEnd))
                        Typeface.BOLD_ITALIC -> result.insertStyle(WebPageTokenStyle.BoldItalic(spanStart, spanEnd))
                    }
                }

                is UnderlineSpan -> {
                    result.insertStyle(WebPageTokenStyle.Underline(spanStart, spanEnd))
                }

                is ForegroundColorSpan -> {
                    result.insertStyle(WebPageTokenStyle.ForegroundColor(Color(span.foregroundColor), spanStart, spanEnd))
                }

                else -> Log.d("yopta", "unknown span type ${span.javaClass.simpleName}")
            }
        }

        return result
    }

    private fun MutableList<WebPageToken>.insertImage(start: Int, url: String) {
        val tokenToSplitIndex = indexOfFirst { start in it }
        val tokenToSplit = getOrNull(tokenToSplitIndex) ?: return
        if (tokenToSplit !is WebPageToken.Text) {
            return
        }
        try {
            val leadingTokenText = tokenToSplit.text.substring(0, start - tokenToSplit.start)
            val leadingToken = WebPageToken.Text(
                text = leadingTokenText,
                start = tokenToSplit.start,
                end = start - 1
            ).obtainStyles(tokenToSplit.styles)

            val imageToken = WebPageToken.Image(url, start)

            val trailingText = tokenToSplit.text.substring(leadingTokenText.length + 1)
            val trailingToken = WebPageToken.Text(
                text = trailingText,
                start = start + 1
            ).obtainStyles(tokenToSplit.styles)

            removeAt(tokenToSplitIndex)
            add(tokenToSplitIndex, leadingToken)
            add(tokenToSplitIndex + 1, imageToken)
            add(tokenToSplitIndex + 2, trailingToken)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun MutableList<WebPageToken>.insertStyle(style: WebPageTokenStyle) {
        val targetTokenIndex = indexOfFirst { style.start in it }
        val targetToken = getOrNull(targetTokenIndex) ?: return
        if (targetToken !is WebPageToken.Text) {
            return
        }

        try {
            removeAt(targetTokenIndex)
            add(
                targetTokenIndex,
                targetToken.copy(styles = targetToken.styles + listOf(style))
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
