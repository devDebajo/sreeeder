package ru.debajo.reader.rss.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.ui.add.AddChannelScreen
import ru.debajo.reader.rss.ui.article.ArticleDetailsScreen
import ru.debajo.reader.rss.ui.channel.ChannelArticles
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

@ExperimentalMaterial3Api
@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity() {

    private val channelsViewModel: ChannelsViewModel by diViewModels()
    private val settingsViewModel: SettingsViewModel by diViewModels()
    private val feedListViewModel: FeedListViewModel by diViewModels()

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
                            feedListViewModel = feedListViewModel
                        )
                    }

                    composable(NavGraph.AddChannel.route) {
                        AddChannelScreen(navController)
                    }

                    composable(NavGraph.ArticlesList.route) {
                        ChannelArticles(NavGraph.ArticlesList.extract(it.arguments), navController)
                    }

                    composable(NavGraph.ArticleDetails.route) {
                        ArticleDetailsScreen(NavGraph.ArticleDetails.extract(it.arguments))
                    }
                }
            }
        }
    }
}
