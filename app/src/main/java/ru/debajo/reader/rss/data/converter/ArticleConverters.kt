package ru.debajo.reader.rss.data.converter

import com.prof.rssparser.Article
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.toDb
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ui.article.model.UiArticle

fun Article.toRemote(channelUrl: String): RemoteArticle {
    return RemoteArticle(
        author = author,
        id = guid,
        title = title,
        image = image ?: itunesArticleData?.image,
        url = link,
        contentHtml = content,
        timestamp = pubDate?.parseDateTimeSafe(),
        channelUrl = RemoteChannelUrl(channelUrl),
        categories = categories,
    )
}

fun RemoteArticle.toDb(channel: RemoteChannel): DbArticle? {
    val url = url
    return DbArticle(
        id = id ?: url ?: return null,
        channelUrl = channel.url.url,
        channelName = channel.name,
        channelImage = channel.image,
        author = author,
        title = title ?: return null,
        image = image,
        url = url ?: return null,
        contentHtml = contentHtml,
        timestamp = timestamp?.toDb(),
        categories = categories.filter { it.isNotEmpty() },
    )
}

fun UiArticle.toDb(): DbArticle {
    val url = url
    return DbArticle(
        id = id,
        channelUrl = "",
        channelName = channelName.orEmpty(),
        channelImage = channelImage,
        author = author,
        title = title,
        image = image,
        url = url,
        contentHtml = rawHtmlContent,
        timestamp = timestamp?.toDb(),
        categories = categories.filter { it.isNotEmpty() },
    )
}

fun DomainArticle.toUi(isNew: Boolean, bookmarked: Boolean = this.bookmarked): UiArticle {
    return UiArticle(
        id = id,
        author = author,
        title = title,
        image = image,
        url = url,
        bookmarked = bookmarked,
        timestamp = timestamp,
        channelImage = channelImage,
        channelName = channelName,
        channelUrl = channelUrl.url,
        categories = categories,
        isNew = isNew,
        rawHtmlContent = contentHtml,
        readPercents = readPercents,
    )
}

fun DbArticle.toDomain(): DomainArticle {
    return DomainArticle(
        id = id,
        author = author,
        title = title,
        image = image,
        url = url,
        contentHtml = contentHtml,
        timestamp = timestamp?.dateTime,
        channelUrl = DomainChannelUrl(channelUrl),
        channelImage = channelImage,
        channelName = channelName,
        bookmarked = false,
        categories = categories,
        readPercents = 0,
    )
}

fun UiArticle.toDomain(): DomainArticle {
    return DomainArticle(
        id = id,
        author = author,
        title = title,
        image = image,
        url = url,
        contentHtml = rawHtmlContent,
        timestamp = timestamp,
        channelUrl = DomainChannelUrl(channelUrl),
        channelImage = channelImage,
        channelName = channelName.orEmpty(),
        bookmarked = bookmarked,
        categories = categories,
        readPercents = readPercents,
    )
}

fun List<Article>.toRemoteList(channelUrl: String): List<RemoteArticle> = map { it.toRemote(channelUrl) }
fun List<DbArticle>.toDomainList(): List<DomainArticle> = map { it.toDomain() }
fun List<RemoteArticle>.toDbList(channel: RemoteChannel): List<DbArticle> = mapNotNull { it.toDb(channel) }
