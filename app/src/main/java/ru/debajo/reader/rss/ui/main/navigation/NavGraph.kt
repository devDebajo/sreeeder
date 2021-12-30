package ru.debajo.reader.rss.ui.main.navigation

import android.content.Intent
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.main.model.ChromeTabsParams


object NavGraph {
    object Main : UnitNavigationNode {

        override val route: String = "MainScreen"

        object Feed : UnitNavigationNode {
            override val route: String = "Feed"
        }

        object Channels : UnitNavigationNode {
            override val route: String = "Channels"
        }

        object Favorites : UnitNavigationNode {
            override val route: String = "Favorites"
        }

        object Settings : UnitNavigationNode {
            override val route: String = "Settings"
        }
    }

    object AddChannel : UnitNavigationNode {
        override val route: String = "AddChannelScreen"
    }

    object ArticlesList : ParcelableNavigationNode<UiChannel> {
        override val route: String = "ChannelArticles"

        override val parameterKey: String = "ChannelId"
    }

    object ChromeTabs : ParcelableNavigationNode<ChromeTabsParams> {
        override val route: String = "ChromeTabs"

        override val parameterKey: String = "Params"

        override fun navigate(navController: NavController, data: ChromeTabsParams) {
            val context = navController.context
            ChromeTabsLauncher.launch(context, data)
        }
    }

    object ShareText : StringNavigationNode {
        override val route: String = "ShareText"

        override val parameterKey: String = "Text"

        override fun navigate(navController: NavController, data: String) {
            navController.context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, data),
                    navController.context.getString(R.string.share_title)
                )
            )
        }
    }
}
