package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.bookmarks.BookmarksList
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.ext.animatedHeight
import ru.debajo.reader.rss.ui.ext.toInt
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.host.ViewModels
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.list.rememberScrollController
import ru.debajo.reader.rss.ui.main.model.ScreenTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsList

val feedTab = ScreenTab(R.string.screen_feed, Icons.Rounded.RssFeed, NavGraph.Main.Feed)
val channelsTab = ScreenTab(R.string.screen_channels, Icons.Rounded.Favorite, NavGraph.Main.Channels)
val bookmarksTab = ScreenTab(R.string.screen_favorites, Icons.Rounded.Bookmark, NavGraph.Main.Favorites)
val settingsTab = ScreenTab(R.string.screen_settings, Icons.Rounded.Settings, NavGraph.Main.Settings)

private val tabs = listOf(feedTab, channelsTab, bookmarksTab, settingsTab)

@Composable
fun MainScreen(
    parentController: NavHostController,
    uiArticleNavigator: UiArticleNavigator
) {
    val navController = rememberNavController()
    val scrollController = rememberScrollController()
    MainScaffold(
        navController = navController,
        scrollController = scrollController,
        onFabClick = { NavGraph.AddChannel.navigate(parentController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = tabs[0].navigation.route
        ) {
            composable(feedTab.navigation.route) {
                val backgroundColor = MaterialTheme.colorScheme.background
                FeedList(
                    innerPadding = innerPadding,
                    scrollController = scrollController,
                    onArticleClick = { uiArticleNavigator.open(it, parentController, backgroundColor) }
                )
            }
            composable(channelsTab.navigation.route) {
                ChannelsList(
                    innerPadding = innerPadding,
                    scrollController = scrollController,
                    onChannelClick = { NavGraph.ArticlesList.navigate(parentController, it) }
                )
            }
            composable(bookmarksTab.navigation.route) {
                val backgroundColor = MaterialTheme.colorScheme.background
                BookmarksList(
                    innerPadding = innerPadding,
                    scrollController = scrollController,
                    onArticleClick = { uiArticleNavigator.open(it, parentController, backgroundColor) }
                )
            }
            composable(settingsTab.navigation.route) {
                SettingsList(innerPadding) {
                    NavGraph.ChromeTabs.navigate(parentController, it)
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    navController: NavController,
    scrollController: ScrollController,
    viewModel: MainViewModel = ViewModels.mainViewModel,
    onFabClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    Scaffold(
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
                    onClick = onFabClick,
                    content = { Icon(Icons.Rounded.Search, contentDescription = null) }
                )
            }
        }
    ) { innerPadding -> content(innerPadding) }
}

@Composable
fun MainTopBar(
    tab: ScreenTab,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    SmallTopAppBar(
        title = {
            Text(
                text = tab.title,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )
        },
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
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
