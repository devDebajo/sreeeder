package ru.debajo.reader.rss.ui.channel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChannelArticles(channel: UiChannel, navController: NavController) {
    val viewModel = diViewModel<ChannelArticlesViewModel>()
    LaunchedEffect(key1 = channel, block = { viewModel.load(channel) })

    val backgroundColor = MaterialTheme.colorScheme.background.colorInt
    val unsubscribeDialogVisible = rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(channel.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    val isSubscribed by viewModel.isSubscribed.collectAsState()
                    TextButton(onClick = {
                        if (isSubscribed) {
                            unsubscribeDialogVisible.value = true
                        } else {
                            viewModel.onSubscribeClick(channel)
                        }
                    }) {
                        val stringRes = if (isSubscribed) R.string.channel_you_subscribed else R.string.channel_subscribe
                        Text(stringResource(stringRes).uppercase())
                    }
                    IconButton(onClick = {
                        viewModel.onShare()
                        NavGraph.ShareText.navigate(navController, channel.url.url)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) {
        Box {
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
                        ChannelArticle(
                            article = articles[index],
                            onFavoriteClick = { viewModel.onFavoriteClick(it) },
                        ) {
                            NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor))
                        }
                    }
                })

            UnsubscribeDialog(channel, viewModel, unsubscribeDialogVisible)
        }
    }
}

@Composable
private fun UnsubscribeDialog(
    channel: UiChannel,
    viewModel: ChannelArticlesViewModel,
    visible: MutableState<Boolean>,
) {
    if (visible.value) {
        AlertDialog(
            onDismissRequest = { visible.value = false },
            confirmButton = {
                TextButton(onClick = { visible.value = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onSubscribeClick(channel)
                    visible.value = false
                }) {
                    Text(stringResource(R.string.channel_unsubscribe))
                }
            },
            title = { Text(stringResource(R.string.channel_unsubscribe_dialog_title)) },
            text = { Text(stringResource(R.string.channel_unsubscribe_dialog_text, channel.name)) }
        )
    }
}
