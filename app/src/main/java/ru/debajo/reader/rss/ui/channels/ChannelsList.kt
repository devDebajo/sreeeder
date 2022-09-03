package ru.debajo.reader.rss.ui.channels

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.list.SreeederList
import ru.debajo.reader.rss.ui.common.rememberEnterAlwaysScrollBehavior
import ru.debajo.reader.rss.ui.ext.addPadding
import ru.debajo.reader.rss.ui.ext.composeColor
import ru.debajo.reader.rss.ui.ext.getColorRoles
import ru.debajo.reader.rss.ui.ext.plus
import ru.debajo.reader.rss.ui.feed.ScrollToTopButton
import ru.debajo.reader.rss.ui.host.ViewModels
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.channelsTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
fun ChannelsList(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    scrollController: ScrollController,
    viewModel: ChannelsViewModel = ViewModels.channelsViewModel,
    forLandscape: Boolean = false,
    onChannelClick: (UiChannel) -> Unit,
    onFeedClick: () -> Unit = {},
) {
    LaunchedEffect("ChannelsList", block = { viewModel.load() })
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (!forLandscape) {
                MainTopBar(
                    tab = channelsTab,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) {
        val channels by viewModel.channels.collectAsState()
        if (channels.isEmpty() && !forLandscape) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                Text(
                    text = stringResource(R.string.channels_is_empty),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 26.dp),
                )
            }
        } else {
            val listScrollState = scrollController.rememberLazyListState(NavGraph.Main.Channels.route)
            ScrollToTopButton(
                listScrollState = listScrollState,
                contentPadding = innerPadding + it,
            ) {
                SreeederList(
                    state = listScrollState,
                    contentPadding = innerPadding.addPadding(bottom = 100.dp) + it,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalSpacing = 16.dp,
                    key = { index -> channels[index].url.url },
                    itemCount = channels.size,
                    itemFactory = { index ->
                        ChannelCard(channel = channels[index]) { channel ->
                            onChannelClick(channel)
                        }
                    },
                    header = if (forLandscape) {
                        { FeedCard(onClick = onFeedClick) }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Composable
private fun FeedCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.screen_feed),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Лента всех каналов",
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
inline fun ChannelCard(
    modifier: Modifier = Modifier,
    channel: UiChannel,
    crossinline onClick: (UiChannel) -> Unit
) {
    val context = LocalContext.current
    val roles = remember(isSystemInDarkTheme()) {
        channel.imageDominantColor?.getColorRoles(context)
    }
    val bgColor = roles?.accentContainer?.composeColor ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    val textColor = roles?.onAccentContainer?.composeColor ?: MaterialTheme.colorScheme.onSurface
    AppCard(
        modifier = modifier.fillMaxWidth(),
        bgColor = bgColor,
        onClick = { onClick(channel) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = channel.name,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (channel.description != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = channel.description,
                    color = textColor,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
