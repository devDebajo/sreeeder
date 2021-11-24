package ru.debajo.reader.rss.domain.channel

import org.joda.time.DateTime
import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.converter.toDbList
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toDomainList
import ru.debajo.reader.rss.data.db.dao.ArticlesDao
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.data.remote.RssLoader
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

    suspend fun getChannelsByUrls(urls: List<String>): List<DomainChannel> {
        val urlsIndices = urls.withIndex().associateBy({ it.value }, { it.index })
        val (actualUrls, nonActualUrls) = splitActualIds(urls)
        val actualChannels = channelsDao.getByUrls(actualUrls).toDomainList()
        val loadedChannels = loadChannelsFromNetwork(nonActualUrls)
        return (actualChannels + loadedChannels).sortedBy { urlsIndices[it.url] ?: 0 }
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
        val html = """
            <p>It’s the final countdown – WebStorm 2021.3 is coming soon! In the meantime, you can try the second release candidate build. Please note that this build requires you to have an <strong>active WebStorm license</strong>. Otherwise, you will need to sign up for a 30-day free trial to install and run this build.</p>
            <p align="center"><a class="jb-download-button" href="https://www.jetbrains.com/webstorm/nextversion">DOWNLOAD WEBSTORM 2021.3 RC</a></p>
            <p>Check out the video from <a href="https://twitter.com/paulweveritt">Paul Everitt</a>, our Developer Advocate, where he goes over the most interesting improvements in this release.</p>
            <p><iframe loading="lazy" title="YouTube video player" src="https://www.youtube.com/embed/Sqy0INe0ikA" width="800" height="450" frameborder="0" allowfullscreen="allowfullscreen"></iframe></p>
            <p>To find out what else has been implemented in WebStorm 2021.3, check out our <a href="https://blog.jetbrains.com/webstorm/tag/webstorm-2021-3/">previous EAP blog posts</a>.</p>
            <p>Please report any issues to our <a href="https://youtrack.jetbrains.com/issues/WEB">issue tracker</a>, and stay tuned for the upcoming release announcement!</p>
            <p><em>The WebStorm team</em></p>
        """.trimIndent()

       return  listOf(
            DomainArticle(
                "random",
                "kek",
                "title",
                "kek",
                html,
                DateTime.now()
            )
        )
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
            .onSuccess { persist(url, it) }
            .mapCatching { it.currentArticles.toDomainList() }
            .getOrElse { emptyList() }
    }

    private suspend fun persist(url: String, channel: RemoteChannel) {
        channelsDao.add(channel.toDb())
        articlesDao.insert(channel.currentArticles.toDbList(url))
        updateCacheKey(url)
    }
}
