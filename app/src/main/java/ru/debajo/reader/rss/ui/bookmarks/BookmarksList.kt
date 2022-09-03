package ru.debajo.reader.rss.ui.bookmarks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.common.list.SreeederList
import ru.debajo.reader.rss.ui.common.rememberEnterAlwaysScrollBehavior
import ru.debajo.reader.rss.ui.feed.ScrollToTopButton
import ru.debajo.reader.rss.ui.feed.model.UiArticleElement
import ru.debajo.reader.rss.ui.host.ViewModels
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.bookmarksTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
fun BookmarksList(
    innerPadding: PaddingValues,
    scrollController: ScrollController,
    viewModel: BookmarksListViewModel = ViewModels.bookmarksListViewModel,
    onArticleClick: (UiArticle) -> Unit,
) {
    LaunchedEffect(key1 = "BookmarksList", block = { viewModel.load() })
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                tab = bookmarksTab,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        val articles by viewModel.articles.collectAsState(emptyList())
        val tabs by viewModel.tabs.collectAsState(emptyList())

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                val contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
                for (tab in tabs) {
                    if (tab.selected) {
                        Button(
                            contentPadding = contentPadding,
                            onClick = { viewModel.onTabClick(tab) },
                            content = { Text(tab.title) }
                        )
                    } else {
                        OutlinedButton(
                            contentPadding = contentPadding,
                            onClick = { viewModel.onTabClick(tab) },
                            content = { Text(tab.title) }
                        )
                    }
                }
            }
            List(
                articles = articles,
                innerPadding = innerPadding,
                scrollController = scrollController,
                viewModel = viewModel,
                onArticleClick = onArticleClick,
            )
        }
    }
}

@Composable
private fun List(
    articles: List<UiArticleElement>,
    innerPadding: PaddingValues,
    scrollController: ScrollController,
    viewModel: BookmarksListViewModel,
    onArticleClick: (UiArticle) -> Unit,
) {
    if (articles.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.bookmarks_is_empty),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 26.dp),
            )
        }
    } else {
        val listScrollState = scrollController.rememberLazyListState(NavGraph.Main.Favorites.route)
        ScrollToTopButton(
            listScrollState = listScrollState,
            contentPadding = innerPadding,
        ) {
            SreeederList(
                verticalSpacing = 12.dp,
                itemCount = articles.size,
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                state = listScrollState,
                key = { index -> articles[index].article.id + articles[index].article.channelName },
                itemFactory = { index ->
                    ChannelArticle(
                        articleElement = articles[index],
                        onFavoriteClick = { viewModel.onFavoriteClick(it) },
                        onClick = onArticleClick
                    )
                }
            )
        }
    }
}
