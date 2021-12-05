package ru.debajo.reader.rss.ui.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.data.remote.RssLoadDbManager
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class FeedListViewModel(
    private val useCase: FeedListUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val rssLoadDbManager: RssLoadDbManager,
) : BaseViewModel() {

    private var refreshingJob: Job? = null
    private val articlesMutable: MutableStateFlow<List<Pair<UiArticle, UiChannel?>>> = MutableStateFlow(emptyList())
    private val isRefreshingMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val articles: StateFlow<List<Pair<UiArticle, UiChannel?>>> = articlesMutable
    val isRefreshing: StateFlow<Boolean> = isRefreshingMutable

    fun load() {
        launch(IO) {
            useCase()
                .map { domain -> domain.map { entry -> entry.article.toUi() to entry.channel?.toUi() } }
                .collectTo(articlesMutable)
        }

        onPullToRefresh(force = false)
    }

    fun onPullToRefresh(force: Boolean = true) {
        refreshingJob?.cancel()
        refreshingJob = launch(IO) {
            rssLoadDbManager.refreshSubscriptions(force = force)
                .map { it is RssLoadDbManager.SubscriptionLoadingState.Refreshing }
                .collectTo(isRefreshingMutable)
        }
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }
}
