package ru.debajo.reader.rss.data.remote

import net.dankito.readability4j.Readability4J
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ru.debajo.reader.rss.data.getBytes
import timber.log.Timber
import java.nio.charset.Charset

class ReadableArticleHelper(private val httpClient: OkHttpClient) {

    suspend fun loadReadableArticleHtml(url: String): String? {
        return runCatching {
            val htmlBytes = httpClient.getBytes(url)
            val charset = detectCharset(String(htmlBytes))
            val document = Jsoup.parse(String(htmlBytes, charset))
            val foreignAgentElement = document.findForeignAgentHtmlElement()
            val readability4J = Readability4J(url, document)
            val article = readability4J.parse()
            if (foreignAgentElement != null) {
                article.articleContent?.prependChild(foreignAgentElement)
            }
            article.content
        }
            .onFailure { Timber.tag("ReadableArticleHelper").e(it) }
            .getOrNull()
    }

    private fun detectCharset(html: String): Charset {
        val charsetName = Jsoup.parse(html).allElements
            .asSequence()
            .filter { it.tagName().equals("meta", true) }
            .mapNotNull { meta -> meta.attr("charset").takeIf { it.isNotEmpty() } }
            .firstOrNull() ?: return Charsets.UTF_8

        return runCatching { Charset.forName(charsetName) }.getOrElse { Charsets.UTF_8 }
    }

    private fun Document.findForeignAgentHtmlElement(): Element? {
        return body().allElements.firstOrNull { it.ownText().contains("иностранного агента", true) }
    }
}