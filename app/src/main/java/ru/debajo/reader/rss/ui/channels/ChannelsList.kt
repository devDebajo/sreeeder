package ru.debajo.reader.rss.ui.channels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import jp.wasabeef.glide.transformations.BlurTransformation
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.AppImage
import ru.debajo.reader.rss.ui.ext.addPadding
import ru.debajo.reader.rss.ui.feed.ScrollTopTopButton
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
fun ChannelsList(
    innerPadding: PaddingValues,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: ChannelsViewModel
) {
    LaunchedEffect("ChannelsList", block = { viewModel.load() })
    Column(modifier = Modifier.fillMaxSize()) {
        val channels by viewModel.channels.collectAsState()
        if (channels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
            ScrollTopTopButton(
                listScrollState = listScrollState,
                contentPadding = innerPadding,
            ) {
                LazyColumn(
                    state = listScrollState,
                    contentPadding = innerPadding.addPadding(bottom = 100.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        count = channels.size,
                        key = { channels[it].url.url }
                    ) {
                        ChannelCard(channel = channels[it]) { channel ->
                            NavGraph.ArticlesList.navigate(navController, channel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
inline fun ChannelCard(
    modifier: Modifier = Modifier,
    channel: UiChannel,
    crossinline onClick: (UiChannel) -> Unit
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(channel) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Box {
                AppImage(
                    url = channel.image,
                    builder = { transform(BlurTransformation(10, 2)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    onFailure = {
                        Box(Modifier.fillMaxSize()) {
                            Text("Не удалось загрузить изображение")
                        }
                    }
                )

                AppImage(
                    url = channel.image,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .align(Alignment.Center),
                    shimmer = false
                )
            }
            val textColor = MaterialTheme.colorScheme.onSurface
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
