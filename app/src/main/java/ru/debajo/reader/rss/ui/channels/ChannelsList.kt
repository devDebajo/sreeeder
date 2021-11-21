package ru.debajo.reader.rss.ui.channels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channels.model.UiChannel

@Composable
fun ChannelsList(
    innerPadding: PaddingValues,
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
                    viewModel.onChannelClick(channel)
                }
            }
        }
    }
}

@Composable
inline fun ChannelCard(channel: UiChannel, crossinline onClick: (UiChannel) -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .clickable { onClick(channel) }
            .fillMaxWidth(),
    ) {
        val textColor = MaterialTheme.colorScheme.onSurface
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = channel.name,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if (channel.description != null) {
                Spacer(modifier = Modifier.height(10.dp))
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
