package ru.debajo.reader.rss.ui.bookmarks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.feed.ScrollToTopButton
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.bookmarksTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BookmarksList(
    innerPadding: PaddingValues,
    navController: NavController,
    scrollController: ScrollController,
    viewModel: BookmarksListViewModel,
    uiArticleNavigator: UiArticleNavigator
) {
    LaunchedEffect(key1 = "BookmarksList", block = { viewModel.load() })
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
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
    ) {
        val articles by viewModel.articles.collectAsState(emptyList())
        val tabs by viewModel.tabs.collectAsState(emptyList())

        Column(Modifier.fillMaxSize()) {
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
                uiArticleNavigator = uiArticleNavigator,
                viewModel = viewModel,
                navController = navController,
            )
        }
    }
}

@Composable
private fun List(
    articles: List<UiArticle>,
    innerPadding: PaddingValues,
    scrollController: ScrollController,
    uiArticleNavigator: UiArticleNavigator,
    viewModel: BookmarksListViewModel,
    navController: NavController,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
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
            LazyColumn(
                state = listScrollState,
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = innerPadding.calculateBottomPadding() + 80.dp,
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
                            onFavoriteClick = { viewModel.onFavoriteClick(it) },
                        ) {
                            uiArticleNavigator.open(it, navController, backgroundColor)
                        }
                    }
                }
            )
        }
    }
}
