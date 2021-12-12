package ru.debajo.reader.rss.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import ru.debajo.reader.rss.ui.list.UiListItem
import ru.debajo.reader.rss.ui.list.uiListItems
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
                Box {
                    LazyColumn(
                        state = state,
                        contentPadding = PaddingValues(
                            top = 12.dp,
                            bottom = innerPadding.calculateBottomPadding(),
                            start = 16.dp,
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        content = { articlesList(articles, backgroundColor, navController, viewModel) }
                    )

                    val hasNewArticles by viewModel.hasNewArticles.collectAsState()
                    NewArticlesIndicator(hasNewArticles) {
                        scrollController.scrollToTop(NavGraph.Main.Feed.route)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.NewArticlesIndicator(visible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopCenter),
        visible = visible,
        enter = slideInVertically()
    ) {
        Row(
            Modifier
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(
                    top = 6.dp,
                    bottom = 6.dp,
                    start = 4.dp,
                    end = 10.dp
                )
                .clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = Icons.Rounded.ExpandLess,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.feed_new_articles),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private fun LazyListScope.articlesList(
    items: List<UiListItem>,
    backgroundColor: Color,
    navController: NavController,
    viewModel: FeedListViewModel
) {
    uiListItems(items) { item ->
        when (item) {
            is UiArticleListItem -> {
                ChannelArticle(
                    article = item.article,
                    onFavoriteClick = { viewModel.onFavoriteClick(it) },
                    onLaunched = { viewModel.onArticleViewed(it) }
                ) {
                    NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor.colorInt))
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