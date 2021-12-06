package ru.debajo.reader.rss.data.remote.load

import android.content.Context
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.debajo.reader.rss.data.converter.toRemote
import ru.debajo.reader.rss.data.remote.load.ext.await
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import java.nio.charset.Charset

class RssLoader(
    private val context: Context,
    private val client: OkHttpClient,
) {

    suspend fun loadChannel(channelUrl: DomainChannelUrl): RemoteChannel {
        val bytes = loadChannelRaw(channelUrl.url)
        val charset = detectCharset(bytes)
        val parser = buildParser(charset)

        return parser.parse(String(bytes, charset)).toRemote(channelUrl.url)
    }

    private fun buildParser(charset: Charset): Parser {
        return Parser.Builder()
            .context(context)
            .charset(charset)
            .build()
    }

    private fun detectCharset(bytes: ByteArray): Charset {
        val utf8Content = String(bytes)
        val match = CHARSET_REGEX.find(utf8Content) ?: return Charsets.UTF_8
        val charset = match.groupValues.getOrNull(1) ?: return Charsets.UTF_8
        return runCatching { Charset.forName(charset) }.getOrElse { Charsets.UTF_8 }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun loadChannelRaw(channelUrl: String): ByteArray {
        return withContext(IO) {
            val request = Request.Builder().url(channelUrl).build()
            val response = client.newCall(request).await()
            response.body?.bytes()!!
        }
    }

    private companion object {
        val CHARSET_REGEX = ".*encoding=\"(.*?)\".*".toRegex()
    }
}
