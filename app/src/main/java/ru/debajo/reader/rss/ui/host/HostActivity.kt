package ru.debajo.reader.rss.ui.host

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.ui.add.AddChannelScreen
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticles
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.main.MainScreen
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

@FlowPreview
@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
class HostActivity : ComponentActivity() {

    private val channelsViewModel: ChannelsViewModel by diViewModels()
    private val settingsViewModel: SettingsViewModel by diViewModels()
    private val feedListViewModel: FeedListViewModel by diViewModels()
    private val bookmarksListViewModel: BookmarksListViewModel by diViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SreeeederTheme {
                NavHost(
                    navController = navController,
                    startDestination = NavGraph.Main.route
                ) {
                    composable(NavGraph.Main.route) {
                        MainScreen(
                            parentController = navController,
                            settingsViewModel = settingsViewModel,
                            channelsViewModel = channelsViewModel,
                            feedListViewModel = feedListViewModel,
                            bookmarksListViewModel = bookmarksListViewModel,
                        )
                    }

                    composable(NavGraph.AddChannel.route) {
                        AddChannelScreen(navController)
                    }

                    composable(NavGraph.ArticlesList.route) {
                        ChannelArticles(NavGraph.ArticlesList.extract(it.arguments), navController)
                    }
                }
            }
        }
    }
}
