package ru.debajo.reader.rss.ui.host

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.ext.getNavigationColor
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.list.rememberScrollController
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.main.bookmarksTab
import ru.debajo.reader.rss.ui.main.model.ScreenTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.main.settingsTab

val landscapeChannelsTab = ScreenTab(R.string.screen_feed, Icons.Rounded.RssFeed, NavGraph.Main.Feed)

private val landscapeTabs = listOf(landscapeChannelsTab, bookmarksTab, settingsTab)

@Composable
fun LandscapeLayout(
    mainViewModel: MainViewModel = ViewModels.mainViewModel,
) {
    val scrollController = rememberScrollController()
    Surface(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Row(Modifier.fillMaxSize()) {
            val feedBadgeCount by mainViewModel.feedBadgeCount.collectAsState()
            NavigationRail(
                containerColor = getNavigationColor(MaterialTheme.colorScheme)
            ) {
                for (tab in landscapeTabs) {
                    Item(
                        tab = tab,
                        badgeCount = if (tab === landscapeChannelsTab) feedBadgeCount else 0,
                        onClick = {},
                        withTitle = true,
                    )
                }
            }

            SecondPane(scrollController)
            ThirdPane(scrollController)
        }
    }
}

@Composable
private fun SecondPane(
    scrollController: ScrollController,
) {
    Box(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(1 / 3f)
    ) {
        ChannelsList(
            scrollController = scrollController,
            forLandscape = true,
            onChannelClick = {

            },
            onFeedClick = {

            },
        )
    }
}

@Composable
private fun ThirdPane(
    scrollController: ScrollController,
) {
    FeedList(
        scrollController = scrollController,
        forLandscape = true
    ) {

    }
}

@Composable
private fun Item(
    tab: ScreenTab,
    selected: Boolean = false,
    badgeCount: Int,
    onClick: () -> Unit,
    withTitle: Boolean,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badgeCount > 0) {
                BadgedBox(badge = { Badge { Text(badgeCount.toString()) } }) {
                    Icon(tab.icon, contentDescription = null)
                }
            } else {
                Icon(tab.icon, contentDescription = null)
            }
        },
        label = if (withTitle) {
            { Text(tab.title, fontSize = 10.sp) }
        } else {
            null
        }
    )
    Spacer(modifier = Modifier.height(20.dp))
}