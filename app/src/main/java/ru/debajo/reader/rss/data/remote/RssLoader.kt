package ru.debajo.reader.rss.data.remote

import com.prof.rssparser.Parser
import ru.debajo.reader.rss.data.converter.toRemote
import ru.debajo.reader.rss.data.remote.model.RemoteChannel

class RssLoader(private val parser: Parser) {

    suspend fun loadChannel(channelUrl: String): RemoteChannel {
        return parser.getChannel(channelUrl).toRemote(channelUrl)
    }
}
