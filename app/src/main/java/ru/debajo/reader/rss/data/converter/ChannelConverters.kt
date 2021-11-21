package ru.debajo.reader.rss.data.converter

import com.prof.rssparser.Channel
import ru.debajo.reader.rss.data.converter.channel.toRemoteList
import ru.debajo.reader.rss.data.db.model.DbChannel
import ru.debajo.reader.rss.data.remote.model.RemoteChannel
import ru.debajo.reader.rss.domain.model.DomainChannel
import ru.debajo.reader.rss.ui.channels.model.UiChannel

fun RemoteChannel.toDb(): DbChannel = DbChannel(url, name, description)

fun RemoteChannel.toDomain(): DomainChannel = DomainChannel(url, name, description)

fun DbChannel.toDomain(): DomainChannel = DomainChannel(url, name, description)

fun DomainChannel.toUi(): UiChannel = UiChannel(url, name, description)

fun Channel.toRemote(channelUrl: String): RemoteChannel {
    return RemoteChannel(
        url = channelUrl,
        name = title!!,
        description = description,
        currentArticles = articles.toRemoteList()
    )
}

fun List<DbChannel>.toDomainList(): List<DomainChannel> = map { it.toDomain() }
fun List<DomainChannel>.toUiList(): List<UiChannel> = map { it.toUi() }
