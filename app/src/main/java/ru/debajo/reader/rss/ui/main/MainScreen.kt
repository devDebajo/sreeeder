package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.bookmarks.BookmarksList
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
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
    bookmarksListViewModel: BookmarksListViewModel
) {
    val navController = rememberNavController()
    MainScaffold(parentController, navController, mainViewModel) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = tabs[0].navigation.route
        ) {
            composable(feedTab.navigation.route) { FeedList(innerPadding, parentController, feedListViewModel) }
            composable(channelsTab.navigation.route) { ChannelsList(innerPadding, parentController, channelsViewModel) }
            composable(favoritesTab.navigation.route) { BookmarksList(innerPadding, parentController, bookmarksListViewModel) }
            composable(settingsTab.navigation.route) { SettingsList(parentController, settingsViewModel) }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainScaffold(
    parentController: NavController,
    navController: NavController,
    viewModel: MainViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    Scaffold(
        topBar = {
            Text(
                text = tabs[selectedTab].title,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                for ((index, tab) in tabs.withIndex()) {
                    Item(
                        tab = tab,
                        selected = currentDestination?.hierarchy?.any { it.route == tab.navigation.route } == true,
                        onClick = {
                            if (selectedTab != index) {
                                viewModel.updateSelectedTab(index)
                                tab.navigation.navigate(navController)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showButton = currentDestination?.hierarchy?.any {
                it.route == channelsTab.navigation.route || it.route == feedTab.navigation.route
            } == true
            if (showButton) {
                FloatingActionButton(
                    onClick = { NavGraph.AddChannel.navigate(parentController) },
                    content = { Icon(Icons.Rounded.Add, contentDescription = null) }
                )
            }
        }
    ) { innerPadding -> content(innerPadding) }
}

@Composable
private fun RowScope.Item(
    tab: ScreenTab,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(tab.icon, contentDescription = null) },
        label = { Text(tab.title, fontSize = 10.sp) }
    )
}
