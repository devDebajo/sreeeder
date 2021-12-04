package ru.debajo.reader.rss.data.converter

import ru.debajo.reader.rss.data.remote.model.RemoteChannelUrl
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ui.channels.model.UiChannelUrl

fun RemoteChannelUrl.toDomain(): DomainChannelUrl = DomainChannelUrl(url)

fun DomainChannelUrl.toUi(): UiChannelUrl = UiChannelUrl(url)

fun DomainChannelUrl.toRemote(): RemoteChannelUrl = RemoteChannelUrl(url)

fun UiChannelUrl.toDomain(): DomainChannelUrl = DomainChannelUrl(url)

fun List<DomainChannelUrl>.unwrap(): List<String> = map { it.url }
