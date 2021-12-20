package ru.debajo.reader.rss.ui.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesNotificationManager
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ViewedArticlesRepository
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.feed.model.FeedListState
import ru.debajo.reader.rss.ui.feed.model.UiFeedTab

class FeedListViewModel(
    private val useCase: FeedListUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val rssLoadDbManager: RssLoadDbManager,
    private val viewedArticlesRepository: ViewedArticlesRepository,
    private val backgroundUpdatesNotificationManager: BackgroundUpdatesNotificationManager,
) : BaseViewModel() {

    private var refreshingJob: Job? = null
    private val stateMutable: MutableStateFlow<FeedListState> = MutableStateFlow(FeedListState())
    private val isRefreshingMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state: MutableStateFlow<FeedListState> = stateMutable
    val isRefreshing: StateFlow<Boolean> = isRefreshingMutable

    init {
        launch(IO) {
            useCase()
                .map { domain -> prepareState(stateMutable.value, domain) }
                .distinctUntilChanged()
                .collectTo(stateMutable)
        }
    }

    fun onPullToRefresh(force: Boolean = true) {
        refreshingJob?.cancel()
        refreshingJob = launch(IO) {
            viewedArticlesRepository.onViewed()
            backgroundUpdatesNotificationManager.cancel()
            rssLoadDbManager.refreshSubscriptions(force = force)
                .map { it is RssLoadDbManager.SubscriptionLoadingState.Refreshing }
                .collectTo(isRefreshingMutable)
        }
    }

    fun onTabClick(tab: UiFeedTab) {
        val currentState = stateMutable.value
        stateMutable.value = currentState.copy(
            selectedTab = fixSelectedTabIfNeed(
                tabs = currentState.tabs,
                newSelectedTab = currentState.tabs.indexOfFirst { it.code == tab.code }.takeIf { it != -1 } ?: 0
            )
        )
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }

    private suspend fun prepareState(
        currentState: FeedListState,
        loadedArticles: List<LoadArticlesUseCase.EnrichedDomainArticle>
    ): FeedListState {
        val viewedArticlesIds = viewedArticlesRepository.getViewedArticlesIds(loadedArticles.map { it.article.id })

        val (newArticles, oldArticles) = loadedArticles.partition { it.article.id !in viewedArticlesIds }
        return when {
            newArticles.isNotEmpty() && oldArticles.isNotEmpty() -> {
                val tabs = listOf(NEW_ARTICLES_TAB, ALL_ARTICLES_TAB)
                currentState.copy(
                    selectedTab = fixSelectedTabIfNeed(tabs, currentState.selectedTab),
                    tabs = tabs,
                    dataSet = mapOf(
                        NEW_ARTICLES_TAB.code to newArticles.convert(),
                        ALL_ARTICLES_TAB.code to oldArticles.convert()
                    )
                )
            }

            newArticles.isEmpty() && oldArticles.isNotEmpty() -> {
                currentState.copy(
                    selectedTab = 0,
                    tabs = emptyList(),
                    dataSet = mapOf(ALL_ARTICLES_TAB.code to oldArticles.convert())
                )
            }

            newArticles.isNotEmpty() && oldArticles.isEmpty() -> {
                currentState.copy(
                    selectedTab = 0,
                    tabs = emptyList(),
                    dataSet = mapOf(NEW_ARTICLES_TAB.code to newArticles.convert())
                )
            }

            else -> {
                currentState.copy(
                    selectedTab = 0,
                    tabs = emptyList(),
                    dataSet = emptyMap()
                )
            }
        }
    }

    private fun fixSelectedTabIfNeed(
        tabs: List<UiFeedTab>,
        newSelectedTab: Int
    ): Int {
        if (newSelectedTab in tabs.indices) {
            return newSelectedTab
        }
        return 0
    }

    private fun List<LoadArticlesUseCase.EnrichedDomainArticle>.convert(): List<UiArticle> {
        return map { entry -> entry.article.toUi(entry.channel?.toUi()) }
            .sortedByDescending { it.timestamp }
    }

    private companion object {
        val NEW_ARTICLES_TAB = UiFeedTab("NEW_ARTICLES_TAB", R.string.feed_new_articles)
        val ALL_ARTICLES_TAB = UiFeedTab("ALL_ARTICLES_TAB", R.string.feed_all_articles)
    }
}
