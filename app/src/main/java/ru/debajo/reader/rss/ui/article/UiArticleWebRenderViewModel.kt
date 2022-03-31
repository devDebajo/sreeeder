package ru.debajo.reader.rss.ui.article

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticleScrollPositionUseCase
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import timber.log.Timber
import kotlin.math.roundToInt

class UiArticleWebRenderViewModel(
    private val readableArticleHelper: ReadableArticleHelper,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val articleScrollPositionUseCase: ArticleScrollPositionUseCase,
    private val appScope: CoroutineScope,
) : BaseViewModel() {

    private var loadJobs: MutableList<Job> = mutableListOf()
    private val stateMutable: MutableStateFlow<UiArticleWebRenderState> = MutableStateFlow(UiArticleWebRenderState.Loading(false))
    private val scrollPositionMutable: MutableStateFlow<Float> = MutableStateFlow(0f)
    val state: StateFlow<UiArticleWebRenderState> = stateMutable
    val scrollPosition: StateFlow<Float> = scrollPositionMutable

    fun load(uiArticle: UiArticle) {
        loadJobs.forEach { it.cancel() }
        loadJobs.clear()
        stateMutable.value = UiArticleWebRenderState.Loading(false)
        loadJobs += launch {
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

        loadJobs += launch {
            val scroll = articleScrollPositionUseCase.getScroll(uiArticle.id)
            if (scroll != null) {
                scrollPositionMutable.value = scroll
            }
        }
    }

    fun toggleBookmarked(uiArticle: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(uiArticle.id)
        }
    }

    fun saveScroll(articleId: String, scroll: Int, maxScroll: Int) {
        val relativeScroll = ((scroll.toFloat() / maxScroll.toFloat()) * 100).roundToInt().coerceIn(0, 100)
        appScope.launch {
            if (relativeScroll == 0 || relativeScroll > 95) {
                articleScrollPositionUseCase.remove(articleId)
            } else {
                articleScrollPositionUseCase.insert(articleId, relativeScroll)
            }
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
