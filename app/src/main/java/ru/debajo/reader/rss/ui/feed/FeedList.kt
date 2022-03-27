package ru.debajo.reader.rss.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainScreenTopBarActions
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.feedTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: FeedListViewModel,
    uiArticleNavigator: UiArticleNavigator
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                tab = feedTab,
                scrollBehavior = scrollBehavior
            ) {
                val feedState by viewModel.state.collectAsState()
                if (feedState.showOnlyNewArticlesButtonVisible) {
                    MainScreenTopBarActions(feedState, viewModel)
                }
            }
        }
    ) {
        val state by viewModel.state.collectAsState()
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        val listScrollState = scrollController.rememberLazyListState(NavGraph.Main.Feed.route)
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
                ScrollTopTopButton(
                    listScrollState = listScrollState,
                    contentPadding = innerPadding,
                ) {
                    LazyColumn(
                        state = listScrollState,
                        contentPadding = PaddingValues(
                            top = 12.dp,
                            bottom = innerPadding.calculateBottomPadding() + 80.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        content = { articlesList(state.articles, backgroundColor, navController, viewModel, uiArticleNavigator) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScrollTopTopButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.scroll_to_top),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listScrollState: LazyListState,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val canScrollToTop = listScrollState.firstVisibleItemScrollOffset > 0
    Box(Modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = contentPadding.calculateBottomPadding() + 16.dp)
                .then(modifier),
            visible = canScrollToTop
        ) {
            Button(
                onClick = { coroutineScope.launch { listScrollState.animateScrollToItem(0) } }
            ) {
                Text(text)
            }
        }
    }
}

private fun LazyListScope.articlesList(
    articles: List<UiArticle>,
    backgroundColor: Color,
    navController: NavController,
    viewModel: FeedListViewModel,
    uiArticleNavigator: UiArticleNavigator
) {
    items(items = articles, key = { it.id + it.channelName }) { article ->
        ChannelArticle(
            article = article,
            onFavoriteClick = { viewModel.onFavoriteClick(it) },
            onView = { viewModel.onArticleViewed(it) }
        ) {
            uiArticleNavigator.open(it, navController, backgroundColor)
        }
    }
}
