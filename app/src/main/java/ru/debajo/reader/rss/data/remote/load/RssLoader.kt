package ru.debajo.reader.rss.data.remote.load

import android.content.Context
import com.prof.rssparser.Parser
import okhttp3.OkHttpClient
import ru.debajo.reader.rss.data.converter.toRemote
import ru.debajo.reader.rss.data.getBytes
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import java.nio.charset.Charset

class RssLoader(
    private val context: Context,
    private val client: OkHttpClient,
) {

    suspend fun loadChannel(channelUrl: DomainChannelUrl): RemoteChannel {
        val bytes = client.getBytes(channelUrl.url)
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
        val content = String(bytes)
        val match = CHARSET_REGEX.find(content) ?: return Charsets.UTF_8
        val charset = match.groupValues.getOrNull(1) ?: return Charsets.UTF_8
        return runCatching { Charset.forName(charset) }.getOrElse { Charsets.UTF_8 }
    }

    private companion object {
        val CHARSET_REGEX = ".*encoding=\"(.*?)\".*".toRegex()
    }
}
