package ru.debajo.reader.rss.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticlesRoute
import ru.debajo.reader.rss.ui.channel.channelArticlesRouteParams
import ru.debajo.reader.rss.ui.channels.ChannelCard
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.Material3TextField
import ru.debajo.reader.rss.ui.navigate

const val AddChannelScreenRoute = "AddChannelScreen"

@Composable
@ExperimentalMaterial3Api
fun AddChannelScreen(
    viewModel: AddChannelScreenViewModel = diViewModel(),
    parentNavController: NavController,
) {
    Scaffold(
        topBar = {
            Text(
                text = stringResource(R.string.add_channel_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            val text by viewModel.text.collectAsState()
            Material3TextField(
                value = text,
                onValueChange = { viewModel.onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.add_channel_placeholder)) }
            )

            AppCard(
                bgColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { viewModel.onLoadClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_channel_load),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            val channel by viewModel.currentChannel.collectAsState()
            channel?.let { localChannel ->
                ChannelCard(localChannel) {
                    parentNavController.popBackStack()
                    parentNavController.navigate(ChannelArticlesRoute, channelArticlesRouteParams(localChannel))
                    viewModel.reset()
                }
            }
        }
    }
}
