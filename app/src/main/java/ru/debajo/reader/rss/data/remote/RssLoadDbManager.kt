package ru.debajo.reader.rss.data.remote

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.converter.toDbList
import ru.debajo.reader.rss.data.converter.toRemote
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import java.util.concurrent.TimeUnit

class RssLoadDbManager(
    private val rssLoader: RssLoader,
    private val articlesDao: ArticlesDao,
    private val channelsDao: ChannelsDao,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
    private val cacheManager: CacheManager,
) : CoroutineScope by CoroutineScope(SupervisorJob() + IO) {

    fun refreshChannel(channelUrl: DomainChannelUrl, force: Boolean): Flow<ChannelLoadingState> {
        return flow {
            emit(ChannelLoadingState.Refreshing)
            if (!force && cacheManager.isActual(createCacheKey(channelUrl), TimeUnit.DAYS.toMillis(1))) {
                emit(ChannelLoadingState.UpToDate)
            } else {
                runCatching { rssLoader.loadChannel(channelUrl) }
                    .mapCatching { it.tryExtractIcon().tryExtractImages() }
                    .onSuccess {
                        persist(channelUrl, it)
                        emit(ChannelLoadingState.UpToDate)
                    }
                    .onFailure { emit(ChannelLoadingState.Error(it)) }
            }
        }
    }

    fun refreshSubscriptions(force: Boolean): Flow<SubscriptionLoadingState> {
        return flow {
            if (!channelsSubscriptionsRepository.hasSubscriptions()) {
                emit(SubscriptionLoadingState.UpToDate)
            } else {
                 emit(SubscriptionLoadingState.Refreshing)
                val channels = channelsSubscriptionsRepository.observe().filter { it.isNotEmpty() }.firstOrNull().orEmpty()
                if (channels.isNotEmpty()) {
                    channels.map { url -> async { refreshChannelIgnoreError(url, force) } }.awaitAll()
                }
                emit(SubscriptionLoadingState.UpToDate)
            }
        }
    }

    private suspend fun refreshChannelIgnoreError(channelUrl: DomainChannelUrl, force: Boolean) {
        refreshChannel(channelUrl, force)
            .filter { it is ChannelLoadingState.Error || it is ChannelLoadingState.UpToDate }
            .firstOrNull()
    }

    private fun RemoteChannel.tryExtractIcon(): RemoteChannel {
        if (image != null) {
            return this
        }
        val newUri = Uri.parse(url.url).buildUpon()
            .clearQuery()
            .path("favicon.ico")
            .build()
        return copy(image = newUri.toString())
    }

    private fun RemoteChannel.tryExtractImages(): RemoteChannel {
        return copy(
            currentArticles = currentArticles.map { article ->
                if (article.image != null) {
                    article
                } else {
                    article.tryExtractImage()
                }
            }
        )
    }

    private fun RemoteArticle.tryExtractImage(): RemoteArticle {
        val contentHtml = contentHtml ?: return this
        val document = Jsoup.parse(contentHtml)
        val url = document.allElements
            .asSequence()
            .filter { it.tagName() == "img" }
            .map { it.attr("src") }
            .filter { it.isNotEmpty() }
            .firstOrNull() ?: return this
        return copy(image = url)
    }

    private suspend fun persist(url: DomainChannelUrl, channel: RemoteChannel) {
        channelsDao.add(channel.toDb())
        articlesDao.insert(channel.currentArticles.toDbList(url.toRemote()))
        cacheManager.saveMarker(createCacheKey(url))
    }

    private fun createCacheKey(url: DomainChannelUrl): String = CHANNEL_URL_CACHE_PREFIX + url.url

    sealed interface SubscriptionLoadingState {
        object Refreshing : SubscriptionLoadingState
        object UpToDate : SubscriptionLoadingState
    }

    sealed interface ChannelLoadingState {
        object Refreshing : ChannelLoadingState
        class Error(val throwable: Throwable) : ChannelLoadingState
        object UpToDate : ChannelLoadingState
    }

    private companion object {
        const val CHANNEL_URL_CACHE_PREFIX = "channel_cache_"
    }
}
