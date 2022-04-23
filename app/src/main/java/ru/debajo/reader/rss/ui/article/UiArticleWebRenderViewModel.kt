package ru.debajo.reader.rss.ui.article

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.debajo.reader.rss.data.converter.toDb
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.data.remote.ReadableArticleHelper
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticleScrollPositionUseCase
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.article.parser.WebPageParser
import timber.log.Timber
import kotlin.math.roundToInt

class UiArticleWebRenderViewModel(
    private val readableArticleHelper: ReadableArticleHelper,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val articleScrollPositionUseCase: ArticleScrollPositionUseCase,
    private val articlesRepository: ArticlesRepository,
    private val appScope: CoroutineScope,
    private val rssLoadDbManager: RssLoadDbManager,
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
                        ?.let {
                            persistIfNeed(uiArticle, it)
                            parseHtml(it.html)
                        }
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

    private suspend fun persistIfNeed(
        uiArticle: UiArticle,
        readableArticle: ReadableArticleHelper.ReadableArticle
    ) {
        if (!articlesRepository.contains(uiArticle.id)) {
            var toPersist = uiArticle
            if (toPersist.image.isNullOrEmpty()) {
                toPersist = toPersist.copy(
                    image = rssLoadDbManager.tryExtractImage(readableArticle.html)
                )
            }
            if (toPersist.title.isEmpty() && !readableArticle.title.isNullOrEmpty()) {
                toPersist = toPersist.copy(title = readableArticle.title)
            }
            articlesRepository.persist(toPersist.toDb())
        }
    }

    fun toggleBookmarked(uiArticle: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(uiArticle.id)
        }
    }

    fun saveScroll(articleId: String, scroll: Int, maxScroll: Int) {
        appScope.launch {
            val relativeScroll = ((scroll.toFloat() / maxScroll.toFloat()) * 100f).takeIf { it.isFinite() } ?: 0f
            val relativeScrollRounded = relativeScroll.roundToInt().coerceIn(0, 100)
            if (relativeScrollRounded == 0 || relativeScrollRounded > 95) {
                articleScrollPositionUseCase.remove(articleId)
            } else {
                articleScrollPositionUseCase.insert(articleId, relativeScrollRounded)
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
