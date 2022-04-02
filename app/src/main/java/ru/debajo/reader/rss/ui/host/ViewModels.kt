package ru.debajo.reader.rss.ui.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import ru.debajo.reader.rss.ui.bookmarks.BookmarksListViewModel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.main.MainViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel

object ViewModels {
    @Composable
    fun WithViewModels(
        settingsViewModel: SettingsViewModel,
        mainViewModel: MainViewModel,
        channelsViewModel: ChannelsViewModel,
        feedListViewModel: FeedListViewModel,
        bookmarksListViewModel: BookmarksListViewModel,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(
            LocalSettingsViewModel provides settingsViewModel,
            LocalMainViewModel provides mainViewModel,
            LocalChannelsViewModel provides channelsViewModel,
            LocalFeedListViewModel provides feedListViewModel,
            LocalBookmarksListViewModel provides bookmarksListViewModel,
        ) {
            content()
        }
    }

    private val LocalSettingsViewModel = staticCompositionLocal<SettingsViewModel>()
    private val LocalMainViewModel = staticCompositionLocal<MainViewModel>()
    private val LocalChannelsViewModel = staticCompositionLocal<ChannelsViewModel>()
    private val LocalFeedListViewModel = staticCompositionLocal<FeedListViewModel>()
    private val LocalBookmarksListViewModel = staticCompositionLocal<BookmarksListViewModel>()

    val settingsViewModel: SettingsViewModel
        @Composable
        get() = LocalSettingsViewModel.current

    val mainViewModel: MainViewModel
        @Composable
        get() = LocalMainViewModel.current

    val channelsViewModel: ChannelsViewModel
        @Composable
        get() = LocalChannelsViewModel.current

    val feedListViewModel: FeedListViewModel
        @Composable
        get() = LocalFeedListViewModel.current

    val bookmarksListViewModel: BookmarksListViewModel
        @Composable
        get() = LocalBookmarksListViewModel.current

    private fun <T> staticCompositionLocal(): ProvidableCompositionLocal<T> {
        return staticCompositionLocalOf { throw IllegalStateException("Default value not provided") }
    }
}