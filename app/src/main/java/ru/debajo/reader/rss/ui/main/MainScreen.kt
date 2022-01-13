package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ext.toInt
import ru.debajo.reader.rss.ui.bookmarks.BookmarksList
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.list.rememberScrollController
import ru.debajo.reader.rss.ui.main.model.ScreenTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsList
import ru.debajo.reader.rss.ui.settings.SettingsViewModel

private val feedTab = ScreenTab(R.string.screen_feed, Icons.Rounded.RssFeed, NavGraph.Main.Feed)
private val channelsTab = ScreenTab(R.string.screen_channels, Icons.Rounded.Favorite, NavGraph.Main.Channels)
private val favoritesTab = ScreenTab(R.string.screen_favorites, Icons.Rounded.Bookmark, NavGraph.Main.Favorites)
private val settingsTab = ScreenTab(R.string.screen_settings, Icons.Rounded.Settings, NavGraph.Main.Settings)

private val tabs = listOf(feedTab, channelsTab, favoritesTab, settingsTab)

@Composable
fun MainScreen(
    parentController: NavController,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    channelsViewModel: ChannelsViewModel,
    feedListViewModel: FeedListViewModel,
    bookmarksListViewModel: BookmarksListViewModel,
    uiArticleNavigator: UiArticleNavigator
) {
    val navController = rememberNavController()
    val scrollController = rememberScrollController()
    MainScaffold(parentController, navController, scrollController, mainViewModel, feedListViewModel) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = tabs[0].navigation.route
        ) {
            composable(feedTab.navigation.route) { FeedList(innerPadding, parentController, scrollController, feedListViewModel, uiArticleNavigator) }
            composable(channelsTab.navigation.route) { ChannelsList(innerPadding, parentController, scrollController, channelsViewModel) }
            composable(favoritesTab.navigation.route) {
                BookmarksList(
                    innerPadding,
                    parentController,
                    scrollController,
                    bookmarksListViewModel,
                    uiArticleNavigator
                )
            }
            composable(settingsTab.navigation.route) { SettingsList(innerPadding, parentController, settingsViewModel) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainScaffold(
    parentController: NavController,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: MainViewModel,
    feedViewModel: FeedListViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    Scaffold(
        topBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tabs[selectedTab].title,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .weight(1f)
                )

                val feedState by feedViewModel.state.collectAsState()
                if (selectedTab == 0 && feedState.showOnlyNewArticlesButtonVisible) {
                    MainScreenTopBarActions(feedState, feedViewModel)
                }
            }
        },
        bottomBar = {
            val showTitles by viewModel.showNavigationTitles.collectAsState()
            NavigationBar(
                modifier = Modifier.animatedHeight(80.dp - showTitles.toInt(positive = 0, negative = 20).dp)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val feedBadgeCount by viewModel.feedBadgeCount.collectAsState()
                for ((index, tab) in tabs.withIndex()) {
                    Item(
                        tab = tab,
                        selected = currentDestination?.hierarchy?.any { it.route == tab.navigation.route } == true,
                        badgeCount = if (tab === feedTab) feedBadgeCount else 0,
                        onClick = {
                            if (selectedTab != index) {
                                viewModel.updateSelectedTab(index)
                                tab.navigation.navigate(navController)
                            } else {
                                scrollController.scrollToTop(tab.navigation.route)
                            }
                        },
                        withTitle = showTitles,
                    )
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showButton = currentDestination?.hierarchy?.any { it.route == channelsTab.navigation.route } == true
            if (showButton) {
                FloatingActionButton(
                    onClick = { NavGraph.AddChannel.navigate(parentController) },
                    content = { Icon(Icons.Rounded.Search, contentDescription = null) }
                )
            }
        }
    ) { innerPadding -> content(innerPadding) }
}

fun Modifier.animatedHeight(height: Dp): Modifier {
    return composed {
        var previousValue by remember { mutableStateOf<Float?>(null) }
        val animatable = remember { androidx.compose.animation.core.Animatable(height.value) }
        LaunchedEffect(key1 = height, block = {
            if (previousValue == null || previousValue != height.value) {
                previousValue = height.value
                animatable.animateTo(height.value)
            }
        })
        height(animatable.value.dp)
    }
}

@Composable
private fun RowScope.Item(
    tab: ScreenTab,
    selected: Boolean = false,
    badgeCount: Int,
    onClick: () -> Unit,
    withTitle: Boolean,
) {
    NavigationBarItem(
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
}
