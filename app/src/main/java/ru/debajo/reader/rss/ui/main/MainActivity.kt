package ru.debajo.reader.rss.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.ui.channels.ChannelsList
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.favorites.FavoritesList
import ru.debajo.reader.rss.ui.feed.FeedList
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val state: MutableState<MainState> = mutableStateOf(MainState())
    private val channelsViewModel: ChannelsViewModel by diViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SreeeederTheme {
                Scaffold(navController) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = tabs[0].route
                    ) {
                        composable(feedTab.route) { FeedList() }
                        composable(channelsTab.route) { ChannelsList(innerPadding, channelsViewModel) }
                        composable(favoritesTab.route) { FavoritesList() }
                    }
                }
            }
        }
    }

    @Composable
    private fun Scaffold(
        navController: NavController,
        content: @Composable (PaddingValues) -> Unit
    ) {
        val state by state
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
                    for ((index, tab) in tabs.withIndex()) {
                        Item(
                            navController = navController,
                            tab = tab,
                            index = index,
                            selected = state.selectedTab == index
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }

    @Composable
    private fun RowScope.Item(
        navController: NavController,
        tab: ScreenTab,
        index: Int,
        selected: Boolean = false
    ) {
        NavigationBarItem(
            selected = selected,
            onClick = {
                state.value = state.value.copy(selectedTab = index)
                navController.navigate(tab.route)
            },
            icon = {
                Icon(tab.icon, contentDescription = null)
            },
            label = {
                Text(tab.title, fontSize = 10.sp)
            }
        )
    }

    private companion object {
        val feedTab = ScreenTab("Лента", Icons.Default.Add, "Feed")
        val channelsTab = ScreenTab("Каналы", Icons.Default.Add, "Channels")
        val favoritesTab = ScreenTab("Избранное", Icons.Default.Add, "Favorites")

        val tabs = listOf(feedTab, channelsTab, favoritesTab)
    }
}
