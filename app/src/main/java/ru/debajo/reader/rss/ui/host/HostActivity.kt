package ru.debajo.reader.rss.ui.host

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.di.rememberFromDi
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.ext.AndroidColor
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.ext.getNavigationColor
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.AppThemeConfig
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

class HostActivity : ComponentActivity() {

    private var navHostController: NavHostController? = null

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
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT

        setContent {
            SreeeederTheme {
                ConfigureSystemColors()
                val navController = rememberNavController()
                navHostController = navController

                LaunchedEffect(key1 = navController, block = { openUrlArticle(intent) })

                if (isLandscape() && BuildConfig.TABLET_SUPPORT) {
                    LandscapeLayout(
                        channelsViewModel = channelsViewModel,
                        feedListViewModel = feedListViewModel,
                        mainViewModel = mainViewModel,
                    )
                } else {
                    PortraitLayout(
                        settingsViewModel = settingsViewModel,
                        mainViewModel = mainViewModel,
                        channelsViewModel = channelsViewModel,
                        feedListViewModel = feedListViewModel,
                        bookmarksListViewModel = bookmarksListViewModel,
                        uiArticleNavigator = uiArticleNavigator,
                        navController = navController
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { openUrlArticle(it) }
    }

    private fun openUrlArticle(intent: Intent) {
        val url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
        intent.removeExtra(Intent.EXTRA_TEXT)
        val navController = navHostController ?: return

        NavGraph.UiArticleWebRender.navigate(navController, UiArticle.fromUrl(url))
    }

    @Composable
    @Suppress("DEPRECATION")
    private fun ConfigureSystemColors() {
        val themeProvider = rememberFromDi<AppThemeProvider>()
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
                snapshotFlow { getNavigationColor(colors) }
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

    @Composable
    private fun isLandscape(): Boolean {
        return LocalContext.current.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, HostActivity::class.java)
        }
    }
}
