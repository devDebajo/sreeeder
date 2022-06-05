package ru.debajo.reader.rss.data.remote.load

import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.data.remote.model.feedly.FeedlyContentType
import ru.debajo.reader.rss.data.remote.service.FeedlyService
import ru.debajo.reader.rss.ext.trimLastSlash
import timber.log.Timber

class ChannelsSearchRepository(
    private val feedlyService: FeedlyService,
) {
    suspend fun search(query: String): List<RemoteChannelUrl> {
        return runCatching {
            feedlyService.search(query, 10).results
                .filter { it.contentType == FeedlyContentType.ARTICLE }
                .mapNotNull { it.feedId?.replacePrefix() }
                .map { RemoteChannelUrl(clearUrl(it)) }
        }
            .onFailure { Timber.e(it) }
            .getOrElse { emptyList() }
    }

    private fun String.replacePrefix(): String {
        return if (startsWith("feed/")) {
            drop(5)
        } else {
            this
        }
    }

    private fun clearUrl(feedlyUrl: String): String {
        val result = if (feedlyUrl.startsWith("feed/")) {
            feedlyUrl.drop("feed/".length)
        } else {
            feedlyUrl
        }.trimLastSlash()
        return if (result.startsWith("http://")) {
            result.replace("http://", "https://")
        } else {
            result
        }
    }
}
