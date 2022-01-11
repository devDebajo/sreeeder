package ru.debajo.reader.rss.ui.feed

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.*
import ru.debajo.reader.rss.data.preferences.UseEmbeddedWebPageRenderPreference
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

class UiArticleNavigator(
    private val useEmbeddedWebPageRenderPreference: UseEmbeddedWebPageRenderPreference
) : CoroutineScope by CoroutineScope(SupervisorJob()) {

    fun open(uiArticle: UiArticle, navController: NavController, backgroundColor: Color = Color.Black) {
        launch(Dispatchers.IO) {
            val openWebView = useEmbeddedWebPageRenderPreference.get()
            withContext(Dispatchers.Main) {
                if (openWebView) {
                    NavGraph.UiArticleWebRender.navigate(navController, uiArticle)
                } else {
                    NavGraph.ChromeTabs.navigate(navController, uiArticle.url.toChromeTabsParams(backgroundColor))
                }
            }
        }
    }
}
