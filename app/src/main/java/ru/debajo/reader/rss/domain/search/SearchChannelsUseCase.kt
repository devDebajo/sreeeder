package ru.debajo.reader.rss.domain.search

import android.net.Uri
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.db.await
import ru.debajo.reader.rss.data.remote.load.ChannelsSearchRepository
import ru.debajo.reader.rss.data.remote.load.HtmlChannelUrlExtractor
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ext.trimLastSlash
import ru.debajo.reader.rss.ext.withLeading
import timber.log.Timber

class SearchChannelsUseCase(
    private val rssLoadDbManager: RssLoadDbManager,
    private val channelsSearchRepository: ChannelsSearchRepository,
    private val htmlChannelUrlExtractor: HtmlChannelUrlExtractor,
) {
    fun search(input: String): Flow<List<DomainChannel>> {
        val httpsUrl = input.replace("http://", "https://")
        return combine(
            tryLoadAsIs(httpsUrl).ignoreError().withLeading(emptyList()),
            tryAddRssOrFeedPath(httpsUrl).ignoreError().withLeading(emptyList()),
            tryExtractFeedFromHtml(httpsUrl).ignoreError().withLeading(emptyList()),
            searchPlainText(input).ignoreError().withLeading(emptyList()),
        ) { a, b, c, d ->
            buildList {
                addAll(a)
                addAll(b)
                addAll(c)
                addAll(d)
            }.distinctBy { it.url.url }
        }
    }

    private fun tryLoadAsIs(url: String): Flow<List<DomainChannel>> {
        return loadChannelInner(url)
            .map { state ->
                when (state) {
                    is RssLoadDbManager.ChannelLoadingState.Error -> emptyList()
                    is RssLoadDbManager.ChannelLoadingState.Refreshing -> emptyList()
                    is RssLoadDbManager.ChannelLoadingState.UpToDate -> listOf(state.channel)
                }
            }
    }

    private fun tryAddRssOrFeedPath(url: String): Flow<List<DomainChannel>> {
        if (!url.startsWith("https")) {
            return emptyFlow()
        }
        return flow {
            val uri = Uri.parse(url)
            val feedUrl = uri.buildUpon().path("feed").toString()
            var channel = loadChannelInner(feedUrl).await()
            if (channel != null) {
                emit(listOf(channel))
            } else {
                val rssUrl = uri.buildUpon().path("rss").toString()
                channel = loadChannelInner(rssUrl).await()
                emit(listOfNotNull(channel))
            }
        }
    }

    private fun tryExtractFeedFromHtml(url: String): Flow<List<DomainChannel>> {
        return flow {
            val urls = htmlChannelUrlExtractor.tryExtractChannelUrl(url)
            emitAll(loadByUrls(urls))
        }
    }

    @OptIn(FlowPreview::class)
    private fun searchPlainText(query: String): Flow<List<DomainChannel>> {
        return flow {
            val urls = channelsSearchRepository.search(query)
            emitAll(loadByUrls(urls))
        }
    }

    private fun loadByUrls(urls: List<RemoteChannelUrl>): Flow<List<DomainChannel>> {
        return combine(urls.map { url ->
            loadChannelInner(url.url)
                .map { (it as? RssLoadDbManager.ChannelLoadingState.UpToDate)?.channel }
        }) { channels ->
            channels.filterNotNull()
        }
    }

    private fun loadChannelInner(url: String): Flow<RssLoadDbManager.ChannelLoadingState> {
        return rssLoadDbManager.refreshChannel(DomainChannelUrl(url.trimLastSlash()), false)
    }

    private fun <T> Flow<List<T>>.ignoreError(): Flow<List<T>> {
        return catch {
            Timber.e(it)
            emit(emptyList())
        }
    }
}
