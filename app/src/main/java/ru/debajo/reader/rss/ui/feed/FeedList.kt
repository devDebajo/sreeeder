package ru.debajo.reader.rss.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: FeedListViewModel,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    Scaffold(Modifier.fillMaxSize()) {
        val state by viewModel.state.collectAsState()
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.onPullToRefresh() },
            indicator = { refreshState, trigger ->
                SwipeRefreshIndicator(
                    state = refreshState,
                    refreshTriggerDistance = trigger,
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            if (state.articles.isEmpty() && !isRefreshing) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.feed_is_empty),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 26.dp),
                    )
                }
            } else {
                val listScrollState = scrollController.rememberLazyListState(NavGraph.Main.Feed.route)
                LazyColumn(
                    state = listScrollState,
                    contentPadding = PaddingValues(
                        top = 12.dp,
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = { articlesList(state.articles, backgroundColor, navController, viewModel) }
                )
            }
        }
    }
}

private fun LazyListScope.articlesList(
    articles: List<UiArticle>,
    backgroundColor: Color,
    navController: NavController,
    viewModel: FeedListViewModel
) {
    items(items = articles, key = { it.id + it.channelName }) { article ->
        ChannelArticle(
            article = article,
            onFavoriteClick = { viewModel.onFavoriteClick(it) },
            onView = { viewModel.onArticleViewed(it) }
        ) {
            NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor.colorInt))
        }
    }
}
