package ru.debajo.reader.rss.data.remote

import net.dankito.readability4j.Readability4J
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ru.debajo.reader.rss.data.getBytes
import ru.debajo.reader.rss.data.remote.load.RssLoader
import timber.log.Timber

class ReadableArticleHelper(private val httpClient: OkHttpClient) {

    suspend fun loadReadableArticleHtml(url: String): String? {
        return runCatching {
            val htmlBytes = httpClient.getBytes(url)
            val charset = RssLoader.detectCharset(String(htmlBytes))
            val html = String(htmlBytes, charset)
            val document = Jsoup.parse(html)
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

    private fun Document.findForeignAgentHtmlElement(): Element? {
        return body().allElements.firstOrNull { it.ownText().contains("иностранного агента", true) }
    }
}