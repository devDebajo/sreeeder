package ru.debajo.reader.rss.ui.article

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import timber.log.Timber

class UiArticleWebRenderViewModel(
    private val readableArticleHelper: ReadableArticleHelper,
    private val analytics: Analytics,
    private val articleBookmarksRepository: ArticleBookmarksRepository
) : BaseViewModel() {

    private var loadJob: Job? = null
    private val stateMutable: MutableStateFlow<UiArticleWebRenderState> = MutableStateFlow(UiArticleWebRenderState.Loading(false))
    val state: StateFlow<UiArticleWebRenderState> = stateMutable

    fun load(uiArticle: UiArticle) {
        loadJob?.cancel()
        analytics.onOpenEmbeddedWebPage()
        stateMutable.value = UiArticleWebRenderState.Loading(false)
        loadJob = launch {
            launch(IO) {
                subscribeBookmarked(uiArticle.id)
            }
            stateMutable.value = if (!uiArticle.rawHtmlContent.isNullOrEmpty()) {
                parseHtml(uiArticle.rawHtmlContent)
            } else {
                withContext(IO) {
                    readableArticleHelper.loadReadableArticleHtml(uiArticle.url)
                        ?.let { parseHtml(it) }
                        ?: UiArticleWebRenderState.Error(stateMutable.value.bookmarked)
                }
            }
        }
    }

    fun toggleBookmarked(uiArticle: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(uiArticle.id)
        }
    }

    private suspend fun subscribeBookmarked(id: String) {
        articleBookmarksRepository.observeById(id).collect { bookmarked ->
            stateMutable.value = when (val currentState = stateMutable.value) {
                is UiArticleWebRenderState.Error -> currentState.copy(bookmarked = bookmarked)
                is UiArticleWebRenderState.Loading -> currentState.copy(bookmarked = bookmarked)
                is UiArticleWebRenderState.Prepared -> currentState.copy(bookmarked = bookmarked)
            }
        }
    }

    private suspend fun parseHtml(html: String): UiArticleWebRenderState {
        return runCatching { withContext(Dispatchers.Default) { WebPageParser.parse(html) } }
            .onFailure { Timber.tag("UiArticleWebRenderViewModel").e(it) }
            .map { tokens ->
                UiArticleWebRenderState.Prepared(
                    bookmarked = stateMutable.value.bookmarked,
                    tokens = tokens,
                )
            }
            .getOrElse { UiArticleWebRenderState.Error(stateMutable.value.bookmarked) }
    }
}
