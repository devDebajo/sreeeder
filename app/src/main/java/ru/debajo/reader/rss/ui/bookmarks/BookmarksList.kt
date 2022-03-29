package ru.debajo.reader.rss.ui.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    val backgroundColor = MaterialTheme.colorScheme.background
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                tab = bookmarksTab,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        val articles by viewModel.articles.collectAsState()
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
}
