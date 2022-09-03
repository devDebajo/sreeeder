package ru.debajo.reader.rss.ui.article

import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticleOfflineContentUseCase
import ru.debajo.reader.rss.domain.article.ArticleScrollPositionUseCase
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle

class UiArticleWebRenderViewModel(
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val articleScrollPositionUseCase: ArticleScrollPositionUseCase,
    private val appScope: CoroutineScope,
    private val articleOfflineContentUseCase: ArticleOfflineContentUseCase,
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

            val tokens = articleOfflineContentUseCase.getWepPageTokens(uiArticle.toDomain())
            stateMutable.value = if (tokens == null) {
                UiArticleWebRenderState.Error(
                    bookmarked = stateMutable.value.bookmarked,
                )
            } else {
                UiArticleWebRenderState.Prepared(
                    bookmarked = stateMutable.value.bookmarked,
                    tokens = tokens,
                )
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
}
