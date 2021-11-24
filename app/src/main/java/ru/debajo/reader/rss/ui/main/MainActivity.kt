package ru.debajo.reader.rss.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.debajo.reader.rss.di.diViewModels
import ru.debajo.reader.rss.ui.add.AddChannelScreen
import ru.debajo.reader.rss.ui.add.AddChannelScreenRoute
import ru.debajo.reader.rss.ui.channel.ChannelArticles
import ru.debajo.reader.rss.ui.channel.ChannelArticlesRoute
import ru.debajo.reader.rss.ui.channel.extractUiChannel
import ru.debajo.reader.rss.ui.channels.ChannelsViewModel
import ru.debajo.reader.rss.ui.settings.SettingsViewModel
import ru.debajo.reader.rss.ui.theme.SreeeederTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val channelsViewModel: ChannelsViewModel by diViewModels()
    private val settingsViewModel: SettingsViewModel by diViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SreeeederTheme {
                NavHost(
                    navController = navController,
                    startDestination = MainScreenRoute
                ) {
                    composable(MainScreenRoute) {
                        MainScreen(
                            parentController = navController,
                            settingsViewModel = settingsViewModel,
                            channelsViewModel = channelsViewModel
                        )
                    }

                    composable(AddChannelScreenRoute) {
                        AddChannelScreen(navController)
                    }

                    composable(ChannelArticlesRoute) {
                        ChannelArticles(extractUiChannel(it.arguments))
                    }
                }
            }
        }
    }
}
