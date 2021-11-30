package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.favorites.FavoritesList
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.main.model.MainState
import ru.debajo.reader.rss.ui.main.model.ScreenTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsList
import ru.debajo.reader.rss.ui.settings.SettingsViewModel

private val feedTab = ScreenTab(R.string.screen_feed, Icons.Rounded.RssFeed, NavGraph.Main.Feed)
private val channelsTab = ScreenTab(R.string.screen_channels, Icons.Rounded.Feed, NavGraph.Main.Channels)
private val favoritesTab = ScreenTab(R.string.screen_favorites, Icons.Rounded.Bookmark, NavGraph.Main.Favorites)
private val settingsTab = ScreenTab(R.string.screen_settings, Icons.Rounded.Settings, NavGraph.Main.Settings)

private val tabs = listOf(feedTab, channelsTab, favoritesTab, settingsTab)

@Composable
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
fun MainScreen(
    parentController: NavController,
    settingsViewModel: SettingsViewModel,
    channelsViewModel: ChannelsViewModel,
    feedListViewModel: FeedListViewModel,
) {
    val navController = rememberNavController()
    MainScaffold(parentController, navController) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = tabs[0].navigation.route
        ) {
            composable(feedTab.navigation.route) { FeedList(innerPadding, parentController, feedListViewModel) }
            composable(channelsTab.navigation.route) { ChannelsList(innerPadding, parentController, channelsViewModel) }
            composable(favoritesTab.navigation.route) { FavoritesList() }
            composable(settingsTab.navigation.route) { SettingsList(settingsViewModel) }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
private fun MainScaffold(
    parentController: NavController,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    // вот это отсюда убрать
    var state by remember { mutableStateOf(MainState()) }
    Scaffold(
        topBar = {
            Text(
                text = tabs[state.selectedTab].title,
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
                            if (state.selectedTab != index) {
                                state = state.copy(selectedTab = index)
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
            if (currentDestination?.hierarchy?.any { it.route == channelsTab.navigation.route } == true) {
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
