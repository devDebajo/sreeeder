package ru.debajo.reader.rss.ui.host

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.di.inject
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
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

class HostActivity : ComponentActivity() {

    private val hostViewModel: HostViewModel by diViewModels()
    private val channelsViewModel: ChannelsViewModel by diViewModels()
    private val settingsViewModel: SettingsViewModel by diViewModels()
    private val feedListViewModel: FeedListViewModel by diViewModels()
    private val bookmarksListViewModel: BookmarksListViewModel by diViewModels()
    private val mainViewModel: MainViewModel by diViewModels()
    private val uiArticleNavigator: UiArticleNavigator by inject()

    private val createDocumentLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.CreateDocument("*/*")) {
        if (it != null) {
            settingsViewModel.writeOpmlDump(it)
        }
    }
    private val openDocumentLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null) {
            settingsViewModel.readOpmlDump(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            hostViewModel.refreshFeed()
        }
        with(settingsViewModel) {
            exportOpmlClickEvent.observe(this@HostActivity) { fileName -> createDocumentLauncher.launch(fileName) }
            importOpmlClickEvent.observe(this@HostActivity) { openDocumentLauncher.launch(arrayOf("*/*")) }
        }

        setContent {
            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()
            SreeeederTheme {
                systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.background)
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
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java)
        }
    }
}
