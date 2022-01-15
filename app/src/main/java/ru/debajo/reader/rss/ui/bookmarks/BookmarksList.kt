package ru.debajo.reader.rss.ui.bookmarks

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
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.feed.ScrollTopTopButton
import ru.debajo.reader.rss.ui.feed.UiArticleNavigator
import ru.debajo.reader.rss.ui.list.ScrollController
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
    Scaffold(Modifier.fillMaxSize()) {
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
