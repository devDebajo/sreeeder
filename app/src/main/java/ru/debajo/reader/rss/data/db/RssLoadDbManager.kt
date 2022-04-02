package ru.debajo.reader.rss.data.db

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import org.jsoup.Jsoup
import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.converter.toDbList
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.data.db.dao.getNonExistIds
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.remote.load.RssLoader
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.article.NewArticlesRepository
import ru.debajo.reader.rss.domain.cache.CacheManager
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import java.util.concurrent.TimeUnit

class RssLoadDbManager(
    private val context: Context,
    private val rssLoader: RssLoader,
    private val articlesDao: ArticlesDao,
    private val channelsDao: ChannelsDao,
    private val channelsSubscriptionsRepository: ChannelsSubscriptionsRepository,
    private val newArticlesRepository: NewArticlesRepository,
    private val cacheManager: CacheManager,
) : CoroutineScope by CoroutineScope(SupervisorJob() + IO) {

    fun refreshChannel(channelUrl: DomainChannelUrl, force: Boolean): Flow<ChannelLoadingState> {
        return flow {
            emit(ChannelLoadingState.Refreshing)
            val channelFromDb = if (!force && cacheManager.isActual(createCacheKey(channelUrl), TimeUnit.DAYS.toMillis(1))) {
                channelsDao.getByUrl(channelUrl.url)
            } else {
                null
            }

            if (channelFromDb != null) {
                emit(ChannelLoadingState.UpToDate(channelFromDb.toDomain()))
            } else {
                runCatching { rssLoader.loadChannel(channelUrl) }
                    .mapCatching { it.tryExtractIcon().tryExtractImages() }
                    .onSuccess {
                        persist(channelUrl, it)
                        emit(ChannelLoadingState.UpToDate(it.toDomain(context)))
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

    fun tryExtractImage(contentHtml: String): String? {
        val document = Jsoup.parse(contentHtml)
        return document.allElements
            .asSequence()
            .filter { it.tagName() == "img" }
            .map { it.attr("src") }
            .filter { it.isNotEmpty() }
            .firstOrNull()
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
        val url = tryExtractImage(contentHtml)
        return copy(image = url)
    }

    private suspend fun persist(url: DomainChannelUrl, channel: RemoteChannel) {
        channelsDao.add(channel.toDb(context))
        val dbArticles = channel.currentArticles.toDbList(channel)
        persistNewArticles(dbArticles, url)
        articlesDao.insert(dbArticles)
        cacheManager.saveMarker(createCacheKey(url))
    }

    private suspend fun persistNewArticles(articles: List<DbArticle>, channelUrl: DomainChannelUrl) {
        val newArticlesIds = articlesDao.getNonExistIds(articles.map { it.id })
        newArticlesRepository.saveNewArticlesIds(channelUrl, newArticlesIds)
    }

    private fun createCacheKey(url: DomainChannelUrl): String = CHANNEL_URL_CACHE_PREFIX + url.url

    sealed interface SubscriptionLoadingState {
        object Refreshing : SubscriptionLoadingState
        object UpToDate : SubscriptionLoadingState
    }

    sealed interface ChannelLoadingState {
        object Refreshing : ChannelLoadingState
        class Error(val throwable: Throwable) : ChannelLoadingState
        class UpToDate(val channel: DomainChannel) : ChannelLoadingState
    }

    private companion object {
        const val CHANNEL_URL_CACHE_PREFIX = "channel_cache_"
    }
}

suspend fun Flow<RssLoadDbManager.ChannelLoadingState>.await(): DomainChannel? {
    return filter { it is RssLoadDbManager.ChannelLoadingState.UpToDate || it is RssLoadDbManager.ChannelLoadingState.Error }
        .map { (it as? RssLoadDbManager.ChannelLoadingState.UpToDate)?.channel }
        .firstOrNull()
}