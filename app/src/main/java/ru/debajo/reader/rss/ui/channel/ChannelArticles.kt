package ru.debajo.reader.rss.ui.channel

import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import ru.debajo.reader.rss.ui.channels.model.UiChannel

const val ChannelArticlesRoute = "ChannelArticles"
private const val ChannelArticlesRouteChannelParam = "ChannelId"

fun channelArticlesRouteParams(channel: UiChannel): Bundle {
    return bundleOf(ChannelArticlesRouteChannelParam to channel)
}

fun extractUiChannel(bundle: Bundle?): UiChannel {
    return bundle?.getParcelable(ChannelArticlesRouteChannelParam)!!
}

@Composable
fun ChannelArticles(channel: UiChannel) {
    Text(channel.name)
}