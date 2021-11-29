package ru.debajo.reader.rss.ui.main

import android.content.Intent
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

object NavGraph {
    object Main : NavigationNode {

        override val route: String = "MainScreen"

        object Feed : NavigationNode {
            override val route: String = "Feed"
        }

        object Channels : NavigationNode {
            override val route: String = "Channels"
        }

        object Favorites : NavigationNode {
            override val route: String = "Favorites"
        }

        object Settings : NavigationNode {
            override val route: String = "Settings"
        }
    }

    object AddChannel : NavigationNode {
        override val route: String = "AddChannelScreen"
    }

    object ArticlesList : ParcelableNavigationNode<UiChannel> {
        override val route: String = "ChannelArticles"

        override val parameterKey: String = "ChannelId"
    }

    object ArticleDetails : ParcelableNavigationNode<UiArticle> {
        override val route: String = "ArticleDetails"

        override val parameterKey: String = "Article"
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
