package ru.debajo.reader.rss.ui.host

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.debajo.reader.rss.ui.add.AddChannelScreen
import ru.debajo.reader.rss.ui.article.UiArticleWebRender
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticles
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.main.MainScreen
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsViewModel

@Composable
fun PortraitLayout(
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel,
    channelsViewModel: ChannelsViewModel,
    feedListViewModel: FeedListViewModel,
    bookmarksListViewModel: BookmarksListViewModel,
    uiArticleNavigator: UiArticleNavigator,
    navController: NavHostController,
) {
    NavHost(
        modifier = Modifier.systemBarsPadding(),
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
                mainViewModel = mainViewModel,
                uiArticleNavigator = uiArticleNavigator,
            )
        }

        composable(NavGraph.AddChannel.route) {
            AddChannelScreen(navController)
        }

        composable(NavGraph.ArticlesList.route) {
            ChannelArticles(NavGraph.ArticlesList.extract(it.arguments), navController, uiArticleNavigator)
        }

        composable(NavGraph.UiArticleWebRender.route) {
            UiArticleWebRender(
                modifier = Modifier.fillMaxWidth(),
                navController = navController,
                uiArticle = NavGraph.UiArticleWebRender.extract(it.arguments)
            )
        }
    }
}