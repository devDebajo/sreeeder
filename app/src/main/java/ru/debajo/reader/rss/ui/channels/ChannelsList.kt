package ru.debajo.reader.rss.ui.channels

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesRoute
import ru.debajo.reader.rss.ui.channel.channelArticlesRouteParams
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.navigate

@Composable
fun ChannelsList(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: ChannelsViewModel = diViewModel()
) {
    LaunchedEffect("ChannelsList", block = { viewModel.load() })
    Column(modifier = Modifier.fillMaxSize()) {
        val channels by viewModel.channels.collectAsState()
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                count = channels.size,
                key = { channels[it].url }
            ) {
                ChannelCard(channels[it]) { channel ->
                    navController.navigate(ChannelArticlesRoute, channelArticlesRouteParams(channel))
                }
            }
        }
    }
}

@Composable
inline fun ChannelCard(channel: UiChannel, crossinline onClick: (UiChannel) -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(channel) }
    ) {
        val textColor = MaterialTheme.colorScheme.onSurface
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Text(
                    text = channel.name,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f),
                )
                Image(
                    painter = rememberImagePainter(channel.image),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentDescription = null
                )
            }
            if (channel.description != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = channel.description,
                    color = textColor,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
