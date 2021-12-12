package ru.debajo.reader.rss.ui.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ViewedArticlesRepository
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.feed.model.UiArticleListItem
import ru.debajo.reader.rss.ui.feed.model.UiNoNewArticlesListItem
import ru.debajo.reader.rss.ui.list.UiListItem

class FeedListViewModel(
    private val useCase: FeedListUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val rssLoadDbManager: RssLoadDbManager,
    private val viewedArticlesRepository: ViewedArticlesRepository,
) : BaseViewModel() {

    private var refreshingJob: Job? = null
    private val articlesMutable: MutableStateFlow<List<UiListItem>> = MutableStateFlow(emptyList())
    private val isRefreshingMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val hasNewArticlesMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val articles: StateFlow<List<UiListItem>> = articlesMutable
    val isRefreshing: StateFlow<Boolean> = isRefreshingMutable
    val hasNewArticles: StateFlow<Boolean> = hasNewArticlesMutable

    init {
        launch(IO) {
            useCase()
                .map { domain -> prepareArticles(domain) }
                .collectTo(articlesMutable)
        }
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

    fun onArticleViewed(article: UiArticle) {
        launch {
            viewedArticlesRepository.onViewed(article.id)
        }
    }

    private suspend fun prepareArticles(domain: List<LoadArticlesUseCase.EnrichedDomainArticle>): List<UiListItem> {
        val viewedArticlesIds = viewedArticlesRepository.getViewedArticlesIds(domain.map { it.article.id })

        val (newArticles, oldArticles) = domain.partition { it.article.id !in viewedArticlesIds }
        if (newArticles.isEmpty() || oldArticles.isEmpty()) {
            return domain.convert()
        }
        return newArticles.convert() + listOf(UiNoNewArticlesListItem) + oldArticles.convert()
    }

    private fun List<LoadArticlesUseCase.EnrichedDomainArticle>.convert(): List<UiListItem> {
        return map { entry -> UiArticleListItem(entry.article.toUi(entry.channel?.toUi())) }
    }
}
