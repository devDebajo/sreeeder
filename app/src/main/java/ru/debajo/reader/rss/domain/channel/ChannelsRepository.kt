package ru.debajo.reader.rss.domain.channel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.converter.toDbList
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.data.remote.RssLoader
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.cache.BaseCacheRepository
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.ext.switchIfEmpty
import java.util.concurrent.TimeUnit

class ChannelsRepository(
    private val channelsDao: ChannelsDao,
    private val articlesDao: ArticlesDao,
    private val rssLoader: RssLoader,
) : BaseCacheRepository() {

    override val durationMs: Long = TimeUnit.DAYS.toMillis(1)

    override val group: String = "ChannelsRepository"

    suspend fun getChannelsByUrls(urls: List<String>): Flow<List<DomainChannel>> {
        return flow {
            val urlsIndices = urls.withIndex().associateBy({ it.value }, { it.index })
            val (actualUrls, nonActualUrls) = splitActualIds(urls)
            val actualChannels = channelsDao.getByUrls(actualUrls).toDomainList()

            val nonActualFromDb = channelsDao.getByUrls(nonActualUrls).toDomainList()
            emit((actualChannels + nonActualFromDb).sortedBy { urlsIndices[it.url] ?: 0 })

            if (nonActualUrls.isNotEmpty()) {
                val loadedChannels = loadChannelsFromNetwork(nonActualUrls)
                emit((actualChannels + loadedChannels).sortedBy { urlsIndices[it.url] ?: 0 })
            }
        }
    }

    suspend fun getChannel(url: String, force: Boolean = false): DomainChannel? {
        return if (!force && isCacheActual(url)) {
            channelsDao.getByUrl(url)?.toDomain()
        } else {
            runCatching { rssLoader.loadChannel(url).toDomain() }
                .switchIfEmpty { channelsDao.getByUrl(url)?.toDomain() }
                .getOrNull()
        }
    }

    suspend fun getArticles(channelUrl: String, force: Boolean = false): List<DomainArticle> {
        return if (!force && isCacheActual(channelUrl)) {
            articlesDao.getArticles(channelUrl).toDomainList()
        } else {
            loadArticlesFromNetwork(channelUrl)
                .takeIf { it.isNotEmpty() }
                ?: articlesDao.getArticles(channelUrl).toDomainList()
        }
    }

    private suspend fun loadChannelsFromNetwork(urls: List<String>): List<DomainChannel> {
        return urls.mapNotNull { loadChannelFromNetwork(it) }
    }

    private suspend fun loadChannelFromNetwork(url: String): DomainChannel? {
        return runCatching { rssLoader.loadChannel(url) }
            .onSuccess { persist(url, it) }
            .mapCatching { it.toDomain() }
            .getOrNull()
    }

    private suspend fun loadArticlesFromNetwork(url: String): List<DomainArticle> {
        return runCatching { rssLoader.loadChannel(url) }
            .mapCatching { it.tryExtractImages() }
            .onSuccess { persist(url, it) }
            .mapCatching { it.currentArticles.toDomainList() }
            .getOrElse { emptyList() }
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

    private suspend fun persist(url: String, channel: RemoteChannel) {
        channelsDao.add(channel.toDb())
        articlesDao.insert(channel.currentArticles.toDbList(url))
        updateCacheKey(url)
    }
}
