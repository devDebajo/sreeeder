package ru.debajo.reader.rss.ui.channel

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.debajo.reader.rss.ui.channels.model.UiChannel

const val ChannelArticlesRoute = "ChannelArticles"

@Composable
fun ChannelArticles(channel: UiChannel) {
    Text(channel.name)
}