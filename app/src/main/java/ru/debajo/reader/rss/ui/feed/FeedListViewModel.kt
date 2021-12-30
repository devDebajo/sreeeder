package ru.debajo.reader.rss.ui.feed

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesNotificationManager
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.NewArticlesUseCase
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.feed.model.FeedListState

class FeedListViewModel(
    private val useCase: FeedListUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val rssLoadDbManager: RssLoadDbManager,
    private val backgroundUpdatesNotificationManager: BackgroundUpdatesNotificationManager,
    private val newArticlesUseCase: NewArticlesUseCase,
) : BaseViewModel() {

    private var refreshingJob: Job? = null
    private val stateMutable: MutableStateFlow<FeedListState> = MutableStateFlow(FeedListState(emptyList(), false))
    private val isRefreshingMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: StateFlow<FeedListState> = stateMutable
    val isRefreshing: StateFlow<Boolean> = isRefreshingMutable

    init {
        launch(IO) {
            useCase()
                .map { domain -> prepareState(state.value, domain) }
                .distinctUntilChanged()
                .collectTo(stateMutable)
        }
        launch(Default) {
            while (true) {
                delay(10_000)
                newArticlesUseCase.saveViewedArticles()
            }
        }
    }

    fun onArticleViewed(article: UiArticle) {
        launch(Default) {
            newArticlesUseCase.onArticleViewed(article)
        }
    }

    fun onPullToRefresh(force: Boolean = true) {
        refreshingJob?.cancel()
        refreshingJob = launch(IO) {
            backgroundUpdatesNotificationManager.cancel()
            newArticlesUseCase.saveViewedArticles()
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

    fun onOnlyNewArticlesClick(checked: Boolean) {
        updateState {
            copy(showOnlyNewArticles = checked)
        }
    }

    override fun onCleared() {
        launch { newArticlesUseCase.saveViewedArticles() }
        super.onCleared()
    }

    private suspend fun prepareState(
        currentState: FeedListState,
        loadedArticles: List<LoadArticlesUseCase.EnrichedDomainArticle>
    ): FeedListState {
        val newArticlesIds = newArticlesUseCase.getNewIds()

        val articles = loadedArticles.map { article ->
            article.article.toUi(
                channel = article.channel?.toUi(),
                isNew = article.article.id in newArticlesIds
            )
        }.sortedByDescending { it.timestamp }
        return currentState.copy(allArticles = articles)
    }

    private fun updateState(block: FeedListState.() -> FeedListState) {
        stateMutable.value = stateMutable.value.block()
    }
}
