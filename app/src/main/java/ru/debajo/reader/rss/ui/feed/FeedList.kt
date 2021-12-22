package ru.debajo.reader.rss.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.feed.model.UiFeedTab
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
            onRefresh = { viewModel.onPullToRefresh() }
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
                Column {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (tab in state.tabs) {
                            FeedTabWidget(tab, state.isSelected(tab)) { viewModel.onTabClick(it) }
                        }
                    }
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
}

@Composable
private fun FeedTabWidget(
    tab: UiFeedTab,
    selected: Boolean,
    onClick: (UiFeedTab) -> Unit
) {
    val bgColor = with(MaterialTheme.colorScheme) {
        if (selected) primary else secondaryContainer
    }

    Box {
        Row(
            Modifier
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(bgColor)
                .clickable(onClick = { onClick(tab) })
                .padding(vertical = 6.dp, horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(tab.textRes),
                color = MaterialTheme.colorScheme.contentColorFor(bgColor),
                fontSize = 12.sp
            )
        }

        if (tab.badge != null) {
            Box(
                modifier = Modifier
                    .offset(y = 7.dp, x = 10.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(vertical = 1.dp, horizontal = 5.dp)
            ) {
                Text(
                    text = tab.badge.toString(),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
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
        ) {
            NavGraph.ChromeTabs.navigate(navController, it.url.toChromeTabsParams(backgroundColor.colorInt))
        }
    }
}
