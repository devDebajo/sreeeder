package ru.debajo.reader.rss.ui.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.list.SreeederList
import ru.debajo.reader.rss.ui.common.rememberSaveableMutableState
import ru.debajo.reader.rss.ui.ext.plus
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
fun ChannelArticles(channel: UiChannel, navController: NavController, uiArticleNavigator: UiArticleNavigator) {
    val viewModel = diViewModel<ChannelArticlesViewModel>()
    LaunchedEffect(key1 = channel, block = { viewModel.load(channel) })
    val backgroundColor = MaterialTheme.colorScheme.background
    val unsubscribeDialogVisible = rememberSaveableMutableState(false)
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
                    val haptic = LocalHapticFeedback.current
                    TextButton(onClick = {
                        if (isSubscribed) {
                            unsubscribeDialogVisible.value = true
                        } else {
                            viewModel.onSubscribeClick(channel)
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }) {
                        val stringRes = if (isSubscribed) R.string.channel_you_subscribed else R.string.channel_subscribe
                        Text(stringResource(stringRes).uppercase())
                    }
                    IconButton(onClick = {
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
            SreeederList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp) + it,
                verticalSpacing = 12.dp,
                itemCount = articles.size,
                key = { index -> articles[index].article.id },
                itemFactory = { index ->
                    ChannelArticle(
                        articleElement = articles[index],
                        onFavoriteClick = { viewModel.onFavoriteClick(it) },
                        onClick = { uiArticleNavigator.open(it, navController, backgroundColor) },
                        onLoadContentClick = { viewModel.loadContent(it) }
                    )
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
