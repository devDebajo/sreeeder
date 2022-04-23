package ru.debajo.reader.rss.data.dump

import be.ceau.opml.OpmlParser
import be.ceau.opml.OpmlWriter
import be.ceau.opml.entity.Body
import be.ceau.opml.entity.Head
import be.ceau.opml.entity.Opml
import be.ceau.opml.entity.Outline
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.data.db.dao.ArticleBookmarksDao
import ru.debajo.reader.rss.data.db.dao.ChannelsDao
import ru.debajo.reader.rss.data.db.model.DbArticle

class OpmlDumper(
    private val channelsDao: ChannelsDao,
    private val articleBookmarksDao: ArticleBookmarksDao,
) {
    suspend fun dump(): String {
        val opml = Opml("1.0", createHead(), Body(createOutlines()))
        return OpmlWriter().write(opml)
    }

    suspend fun parseChannelsUrls(opmlRaw: String): List<String> {
        return withContext(Default) {
            val opml = OpmlParser().parse(opmlRaw)
            opml.body.outlines
                .filter { it.getAttribute("type") == "rss" }
                .mapNotNull { it.getAttribute("xmlUrl") }
        }
    }

    suspend fun parseBookmarks(opmlRaw: String): List<DbArticle> {
        return withContext(Default) {
            val opml = OpmlParser().parse(opmlRaw)
            opml.body.outlines
                .filter { it.getAttribute("type") == "bookmark" }
                .mapNotNull { it.tryParseDbArticle() }
        }
    }

    private fun Outline.tryParseDbArticle(): DbArticle? {
        val id: String? = getAttribute("id")
        val url: String? = getAttribute("url")
        val channelUrl: String? = getAttribute("channelUrl")
        val title: String? = getAttribute("title")
        val image: String? = getAttribute("image")
        val timestamp: String? = getAttribute("timestamp")
        return DbArticle(
            id = id ?: url ?: return null,
            channelUrl = channelUrl ?: return null,
            channelName = "",
            channelImage = null,
            author = null,
            title = title.orEmpty(),
            image = image,
            url = url ?: return null,
            contentHtml = null,
            timestamp = null, // TODO починить парсинг даты
            categories = emptyList()
        )
    }

    private suspend fun createOutlines(): List<Outline> {
        val channels = channelsDao.getAllSubscribed()
        val subscriptionOutlines = channels.map { channel ->
            Outline(
                mapOf(
                    "type" to "rss",
                    "title" to channel.name,
                    "description" to channel.description,
                    "xmlUrl" to channel.url,
                ),
                emptyList()
            )
        }
        val articlesOutlines = articleBookmarksDao.getArticles().map { article ->
            Outline(
                mapOf(
                    "type" to "bookmark",
                    "id" to article.id,
                    "channelUrl" to article.channelUrl,
                    "title" to article.title,
                    "image" to article.image,
                    "timestamp" to article.timestamp?.dateTime?.toString(),
                    "url" to article.url,
                ),
                emptyList()
            )
        }
        return subscriptionOutlines + articlesOutlines
    }

    private fun createHead(): Head {
        return Head(
            "Sreeeder OPML RSS feeds dump",
            DateTime.now(DateTimeZone.UTC).toString(),
            DateTime.now(DateTimeZone.UTC).toString(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}
