package ru.debajo.reader.rss.data.converter

import com.prof.rssparser.Article
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.debajo.reader.rss.data.converter.channel.parseDateTimeSafe
import ru.debajo.reader.rss.data.db.model.DbArticle
import ru.debajo.reader.rss.data.db.model.toDb
import ru.debajo.reader.rss.data.remote.model.RemoteArticle
import ru.debajo.reader.rss.domain.model.DomainArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle

fun Article.toRemote(): RemoteArticle {
    return RemoteArticle(
        author = author,
        id = guid,
        title = title,
        descriptionHtml = description,
        contentHtml = content,
        timestamp = pubDate?.parseDateTimeSafe()
    )
}

fun RemoteArticle.toDomain(): DomainArticle? {
    return DomainArticle(
        id = id ?: return null,
        author = author ?: return null,
        title = title ?: return null,
        descriptionHtml = descriptionHtml ?: return null,
        contentHtml = contentHtml ?: return null,
        timestamp = timestamp ?: return null
    )
}

fun RemoteArticle.toDb(channelUrl: String): DbArticle? {
    return DbArticle(
        id = id ?: return null,
        channelUrl = channelUrl,
        author = author ?: return null,
        title = title ?: return null,
        descriptionHtml = descriptionHtml ?: return null,
        contentHtml = contentHtml ?: return null,
        timestamp = timestamp?.toDb() ?: return null,
    )
}

suspend fun DomainArticle.toUi(): UiArticle {
    return UiArticle(
        id = id,
        author = author,
        title = title,
        description = parseHtml(descriptionHtml),
        content = parseHtml(contentHtml),
        timestamp = timestamp
    )
}

private suspend fun parseHtml(html: String): Document {
    return withContext(Default) { Jsoup.parse(html) }
}

fun DbArticle.toDomain(): DomainArticle {
    return DomainArticle(
        id = id,
        author = author,
        title = title,
        descriptionHtml = descriptionHtml,
        contentHtml = contentHtml,
        timestamp = timestamp.dateTime
    )
}

fun List<Article>.toRemoteList(): List<RemoteArticle> = map { it.toRemote() }
fun List<DbArticle>.toDomainList(): List<DomainArticle> = map { it.toDomain() }

@JvmName("toDomainListRemoteArticle")
fun List<RemoteArticle>.toDomainList(): List<DomainArticle> = mapNotNull { it.toDomain() }
fun List<RemoteArticle>.toDbList(channelUrl: String): List<DbArticle> = mapNotNull { it.toDb(channelUrl) }

suspend fun List<DomainArticle>.toUiList(): List<UiArticle> = map { it.toUi() }