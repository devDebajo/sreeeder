package ru.debajo.reader.rss.data.converter

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.prof.rssparser.Channel
import ru.debajo.reader.rss.data.db.model.DbChannel
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.ext.loadDominantColorFromImage

suspend fun RemoteChannel.toDb(context: Context): DbChannel {
    val dominantColor = image?.loadDominantColorFromImage(context)
    return DbChannel(url.url, name, image, dominantColor, description)
}

suspend fun RemoteChannel.toDomain(context: Context): DomainChannel {
    val dominantColor = image?.loadDominantColorFromImage(context)
    return DomainChannel(url.toDomain(), name, image, dominantColor, description)
}

fun DbChannel.toDomain(): DomainChannel = DomainChannel(
    url = DomainChannelUrl(url),
    name = name,
    image = image,
    imageDominantColor = imageDominantColor,
    description = description
)

fun DomainChannel.toUi(): UiChannel = UiChannel(url.toUi(), name, image, imageDominantColor?.let { Color(it) }, description)

fun UiChannel.toDomain(): DomainChannel = DomainChannel(url.toDomain(), name, image, imageDominantColor?.colorInt, description)

fun Channel.toRemote(channelUrl: String): RemoteChannel {
    return RemoteChannel(
        url = RemoteChannelUrl(channelUrl),
        name = title!!,
        description = description,
        image = image?.url,
        currentArticles = articles.toRemoteList(channelUrl)
    )
}

fun List<DbChannel>.toDomainList(): List<DomainChannel> = map { it.toDomain() }
fun List<DomainChannel>.toUiList(): List<UiChannel> = map { it.toUi() }
