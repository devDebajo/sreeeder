package ru.debajo.reader.rss.data.remote.load

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import ru.debajo.reader.rss.data.remote.load.ext.await
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl

class HtmlChannelUrlExtractor(
    private val okHttpClient: OkHttpClient,
) {
    suspend fun tryExtractChannelUrl(siteUrl: String): List<RemoteChannelUrl> {
        return runCatching {
            withContext(IO) {
                val request = Request.Builder()
                    .get()
                    .url(siteUrl)
                    .build()
                val response = okHttpClient.newCall(request).await()
                val rawText = response.body?.byteStream()?.bufferedReader()?.use { it.readText() }
                parseHtml(rawText.orEmpty()).map { RemoteChannelUrl(it) }.toList()
            }
        }.getOrElse { emptyList() }
    }

    private fun parseHtml(rawHtml: String): Sequence<String> {
        return Jsoup.parse(rawHtml)
            .allElements
            .asSequence()
            .filter { element -> element.attributes().any { it.key == "type" && it.value.startsWith("application/rss") } }
            .mapNotNull { it.attr("href") }
    }
}