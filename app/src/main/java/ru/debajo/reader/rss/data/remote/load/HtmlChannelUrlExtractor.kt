package ru.debajo.reader.rss.data.remote.load

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import ru.debajo.reader.rss.data.getRawText
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.ext.trimLastSlash

class HtmlChannelUrlExtractor(
    private val okHttpClient: OkHttpClient,
) {
    suspend fun tryExtractChannelUrl(siteUrl: String): List<RemoteChannelUrl> {
        return runCatching {
            withContext(IO) {
                val rawText = okHttpClient.getRawText(siteUrl)
                parseHtml(rawText).map { RemoteChannelUrl(it) }
            }
        }.getOrElse { emptyList() }
    }

    private fun parseHtml(rawHtml: String): List<String> {
        return Jsoup.parse(rawHtml)
            .allElements
            .asSequence()
            .filter { element -> element.attributes().any { it.key == "type" && it.value.startsWith("application/rss") } }
            .mapNotNull { it.attr("href").trimLastSlash() }
            .toList()
    }
}