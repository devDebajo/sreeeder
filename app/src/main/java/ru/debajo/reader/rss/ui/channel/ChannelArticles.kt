package ru.debajo.reader.rss.ui.channel

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

const val ChannelArticlesRoute = "ChannelArticles"
private const val ChannelArticlesRouteChannelParam = "ChannelId"

fun channelArticlesRouteParams(channel: UiChannel): Bundle {
    return bundleOf(ChannelArticlesRouteChannelParam to channel)
}

fun extractUiChannel(bundle: Bundle?): UiChannel {
    return bundle?.getParcelable(ChannelArticlesRouteChannelParam)!!
}

@ExperimentalMaterial3Api
@Composable
fun ChannelArticles(channel: UiChannel) {
    val viewModel = diViewModel<ChannelArticlesViewModel>()
    LaunchedEffect(key1 = channel, block = {
        viewModel.load(channel)
    })
    Scaffold {
        val articles by viewModel.articles.collectAsState()
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            content = {
                items(
                    count = articles.size,
                    key = { index -> articles[index].id }
                ) { index ->
                    ChannelArticle(article = articles[index])
                }
            })
    }
}
