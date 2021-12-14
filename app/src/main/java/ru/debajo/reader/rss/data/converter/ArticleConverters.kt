package ru.debajo.reader.rss.data.converter

import com.prof.rssparser.Article
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.toDb
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

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

fun RemoteArticle.toDomain(): DomainArticle? {
    return DomainArticle(
        id = id ?: return null,
        author = author,
        title = title ?: return null,
        url = url ?: return null,
        contentHtml = contentHtml,
        timestamp = timestamp,
        image = image,
        bookmarked = false,
        channelUrl = channelUrl.toDomain(),
        categories = categories,
    )
}

fun RemoteArticle.toDb(channelUrl: RemoteChannelUrl): DbArticle? {
    return DbArticle(
        id = id ?: return null,
        channelUrl = channelUrl.url,
        author = author,
        title = title ?: return null,
        image = image,
        url = url ?: return null,
        contentHtml = contentHtml,
        timestamp = timestamp?.toDb(),
        categories = categories.filter { it.isNotEmpty() },
    )
}

fun DomainArticle.toUi(channel: UiChannel?): UiArticle {
    return UiArticle(
        id = id,
        author = author,
        title = title,
        image = image,
        url = url,
        bookmarked = bookmarked,
        timestamp = timestamp,
        channelImage = channel?.image,
        channelName = channel?.name,
        categories = categories,
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
        bookmarked = false,
        categories = categories,
    )
}

fun List<Article>.toRemoteList(channelUrl: String): List<RemoteArticle> = map { it.toRemote(channelUrl) }
fun List<DbArticle>.toDomainList(): List<DomainArticle> = map { it.toDomain() }

@JvmName("toDomainListRemoteArticle")
fun List<RemoteArticle>.toDomainList(): List<DomainArticle> = mapNotNull { it.toDomain() }
fun List<RemoteArticle>.toDbList(channelUrl: RemoteChannelUrl): List<DbArticle> = mapNotNull { it.toDb(channelUrl) }

fun List<DomainArticle>.toUiList(channel: UiChannel?): List<UiArticle> = map { it.toUi(channel) }
