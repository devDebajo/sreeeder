package ru.debajo.reader.rss.ui.host

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.di.getFromDi
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.ui.add.AddChannelScreen
import ru.debajo.reader.rss.ui.article.UiArticleWebRender
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channel.ChannelArticles
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.main.MainScreen
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.AppThemeConfig
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import ru.debajo.reader.rss.ui.theme.SreeeederTheme
import kotlin.math.ln

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        setContent {
            val navController = rememberNavController()
            SreeeederTheme {
                ConfigureSystemColors()
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
        }
    }

    @Composable
    @Suppress("DEPRECATION")
    private fun ConfigureSystemColors() {
        val themeProvider = remember { getFromDi<AppThemeProvider>() }
        val colors = MaterialTheme.colorScheme
        val insetsControllerCompat = remember { WindowInsetsControllerCompat(window, window.decorView) }
        LaunchedEffect(key1 = colors, block = {
            launch {
                snapshotFlow { colors.surface }
                    .collect {
                        window.setBackgroundDrawable(ColorDrawable(it.colorInt))
                        insetsControllerCompat.isAppearanceLightStatusBars = !themeProvider.currentAppThemeConfig.value.isActuallyDark()
                    }
            }

            launch {
                snapshotFlow {
                    val alpha = ((4.5f * ln(3.dp.value + 1)) + 2f) / 100f
                    colors.surfaceTint.copy(alpha = alpha).compositeOver(colors.surface)
                }
                    .collect {
                        window.navigationBarColor = it.colorInt
                        insetsControllerCompat.isAppearanceLightNavigationBars = !themeProvider.currentAppThemeConfig.value.isActuallyDark()
                    }
            }
        })
    }

    private fun AppThemeConfig.isActuallyDark(): Boolean {
        return when (theme) {
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
            AppTheme.AUTO -> isSystemInDarkTheme()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java)
        }
    }
}
