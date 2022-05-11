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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.common.StaggeredRecycler
import ru.debajo.reader.rss.ui.ext.plus
import ru.debajo.reader.rss.ui.feed.model.FeedListState
import ru.debajo.reader.rss.ui.host.ViewModels
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainScreenTopBarActions
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.feedTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    scrollController: ScrollController,
    viewModel: FeedListViewModel = ViewModels.feedListViewModel,
    forLandscape: Boolean = false,
    onArticleClick: (UiArticle) -> Unit,
) {
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
                Box(Modifier.fillMaxSize().padding(it)) {
                    Text(
                        text = stringResource(R.string.feed_is_empty),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 26.dp),
                    )
                }
            } else {
                if (forLandscape) {
                    LandscapeList(
                        state = state,
                        viewModel = viewModel,
                        onArticleClick = onArticleClick,
                    )
                } else {
                    ScrollToTopButton(
                        listScrollState = listScrollState,
                        contentPadding = innerPadding + it,
                    ) {
                        PortraitList(
                            innerPadding = innerPadding + it,
                            lazyListState = listScrollState,
                            state = state,
                            viewModel = viewModel,
                            onArticleClick = onArticleClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LandscapeList(
    state: FeedListState,
    viewModel: FeedListViewModel,
    onArticleClick: (UiArticle) -> Unit
) {
    StaggeredRecycler(
        modifier = Modifier.fillMaxSize(),
        spanCount = 2,
        data = state.articles,
        keyEquality = { a, b -> a.id == b.id },
        content = { article ->
            Box(Modifier.padding(8.dp)) {
                ChannelArticle(
                    article = article,
                    onFavoriteClick = { },
                    onView = { },
                    onClick = { }
                )
            }
        }
    )
}

@Composable
private fun PortraitList(
    innerPadding: PaddingValues,
    lazyListState: LazyListState,
    state: FeedListState,
    viewModel: FeedListViewModel,
    onArticleClick: (UiArticle) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 80.dp,
            start = 16.dp,
            end = 16.dp
        ) + innerPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = { articlesList(state.articles, viewModel, onArticleClick) }
    )
}

@Composable
fun ScrollToTopButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.scroll_to_top),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listScrollState: LazyListState,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val direction by detectScrollDirection(listScrollState)
    val canScrollToTop = direction == 1
    Box(Modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .then(modifier),
            visible = canScrollToTop
        ) {
            Button(
                modifier = Modifier
                    .padding(bottom = contentPadding.calculateBottomPadding() + 16.dp),
                onClick = { coroutineScope.launch { listScrollState.animateScrollToItem(0) } }
            ) {
                Text(text)
            }
        }
    }
}

@Composable
private fun detectScrollDirection(state: LazyListState): State<Int> {
    val result = remember { mutableStateOf(0) }
    LaunchedEffect(key1 = state, block = {
        val heights = HashMap<Int, Int>()
        var previousScroll = 0
        combine(
            snapshotFlow { state.firstVisibleItemIndex },
            snapshotFlow { state.firstVisibleItemScrollOffset },
        ) { firstVisibleItemIndex, firstVisibleItemScrollOffset ->
            firstVisibleItemIndex to firstVisibleItemScrollOffset
        }.collect { (firstVisibleItemIndex, firstVisibleItemScrollOffset) ->
            state.layoutInfo.visibleItemsInfo.forEach { info ->
                heights[info.index] = info.size
            }

            var height = 0
            for (i in heights.keys) {
                if (i < firstVisibleItemIndex) {
                    height += heights.getValue(i)
                }
            }
            height += firstVisibleItemScrollOffset
            result.value = when {
                firstVisibleItemIndex + firstVisibleItemScrollOffset == 0 -> 0
                previousScroll - height < 0 -> -1
                else -> 1
            }
            previousScroll = height
        }
    })
    return result
}

private fun LazyListScope.articlesList(
    articles: List<UiArticle>,
    viewModel: FeedListViewModel,
    onArticleClick: (UiArticle) -> Unit,
) {
    items(items = articles, key = { it.id + it.channelName }) { article ->
        ChannelArticle(
            article = article,
            onFavoriteClick = { viewModel.onFavoriteClick(it) },
            onView = { viewModel.onArticleViewed(it) },
            onClick = onArticleClick
        )
    }
}

