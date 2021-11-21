package ru.debajo.reader.rss.ui.channels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channels.model.UiChannel

@Composable
fun ChannelsList(
    viewModel: ChannelsViewModel = diViewModel()
) {
    LaunchedEffect("ChannelsList", block = {
        viewModel.load()
    })
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Каналы",
            fontSize = 36.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        val channels by viewModel.channels.collectAsState()
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
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
    Card(
        modifier = Modifier
            .clickable { onClick(channel) }
            .fillMaxWidth(),
        backgroundColor = Color(0xFFE1F3DB),
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFDADADA)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = channel.name,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if (channel.description != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = channel.description,
                    color = Color.Black,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
