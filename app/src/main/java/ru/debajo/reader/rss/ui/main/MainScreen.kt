package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Feed
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material.icons.rounded.Settings
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
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.add.AddChannelScreenRoute
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.favorites.FavoritesList
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.settings.SettingsList
import ru.debajo.reader.rss.ui.settings.SettingsViewModel

private val feedTab = ScreenTab(R.string.screen_feed, Icons.Rounded.RssFeed, "Feed")
private val channelsTab = ScreenTab(R.string.screen_channels, Icons.Rounded.Feed, "Channels")
private val favoritesTab = ScreenTab(R.string.screen_favorites, Icons.Rounded.Favorite, "Favorites")
private val settingsTab = ScreenTab(R.string.screen_settings, Icons.Rounded.Settings, "Settings")

private val tabs = listOf(feedTab, channelsTab, favoritesTab, settingsTab)

const val MainScreenRoute = "MainScreen"

@Composable
@ExperimentalMaterial3Api
fun MainScreen(
    parentController: NavController,
    settingsViewModel: SettingsViewModel,
    channelsViewModel: ChannelsViewModel,
) {
    val navController = rememberNavController()
    MainScaffold(parentController, navController) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = tabs[0].route
        ) {
            composable(feedTab.route) { FeedList() }
            composable(channelsTab.route) { ChannelsList(innerPadding, parentController, channelsViewModel) }
            composable(favoritesTab.route) { FavoritesList() }
            composable(settingsTab.route) { SettingsList(settingsViewModel) }
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
    var state by remember { mutableStateOf(MainState()) }
    Scaffold(
        topBar = {
            Column {
                Text(
                    text = tabs[state.selectedTab].title,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                for ((index, tab) in tabs.withIndex()) {
                    Item(
                        tab = tab,
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            if (state.selectedTab != index) {
                                state = state.copy(selectedTab = index)
                                navController.navigate(tab.route)
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (state.selectedTab == tabs.indexOf(channelsTab)) {
                FloatingActionButton(
                    onClick = { parentController.navigate(AddChannelScreenRoute) },
                    content = { Icon(Icons.Default.Add, contentDescription = null) }
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
