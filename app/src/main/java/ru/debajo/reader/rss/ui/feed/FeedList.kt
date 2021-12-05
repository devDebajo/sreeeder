package ru.debajo.reader.rss.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: FeedListViewModel
) {
    LaunchedEffect(key1 = "FeedList", block = { viewModel.load() })
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = stringResource(R.string.feed_is_empty),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 26.dp),
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = 12.dp,
                        bottom = innerPadding.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = {
                        items(
                            count = articles.size,
                            key = { index -> articles[index].id + articles[index].channelName }
                        ) { index ->
                            ChannelArticle(
                                article = articles[index],
                                onFavoriteClick = { viewModel.onFavoriteClick(it) }
                            ) {
                                NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor))
                            }
                        }
                    }
                )
            }
        }
    }
}
