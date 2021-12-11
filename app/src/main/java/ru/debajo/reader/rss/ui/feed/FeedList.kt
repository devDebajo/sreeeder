package ru.debajo.reader.rss.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.feed.model.UiArticleListItem
import ru.debajo.reader.rss.ui.feed.model.UiNoNewArticlesListItem
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.list.uiListItems
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: FeedListViewModel
) {
    val backgroundColor = MaterialTheme.colorScheme.background.colorInt
    Scaffold(Modifier.fillMaxSize()) {
        val articles by viewModel.articles.collectAsState()
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        SwipeRefresh(
            modifier = Modifier.fillMaxSize(),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.onPullToRefresh() }
        ) {
            if (articles.isEmpty() && !isRefreshing) {
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
                val state = scrollController.rememberLazyListState(NavGraph.Main.Feed.route)
                LazyColumn(
                    state = state,
                    contentPadding = PaddingValues(
                        top = 12.dp,
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = {
                        uiListItems(articles) { item ->
                            when (item) {
                                is UiArticleListItem -> {
                                    ChannelArticle(
                                        article = item.article,
                                        onFavoriteClick = { viewModel.onFavoriteClick(it) },
                                        onLaunched = { viewModel.onArticleViewed(it) }
                                    ) {
                                        NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor))
                                    }
                                }

                                is UiNoNewArticlesListItem -> {
                                    AppCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 40.dp, horizontal = 16.dp),
                                        onClick = { viewModel.onPullToRefresh() },
                                    ) {
                                        Row(
                                            modifier = Modifier.align(Alignment.Center),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                                            Spacer(Modifier.size(8.dp))
                                            Text(stringResource(R.string.feed_no_new_articles))
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
