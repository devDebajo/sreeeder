package ru.debajo.reader.rss.ui.bookmarks

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticleOfflineContentUseCase
import ru.debajo.reader.rss.domain.article.ArticleScrollPositionUseCase
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.feed.model.UiArticleElement

@SuppressLint("StaticFieldLeak")
class BookmarksListViewModel(
    private val context: Context,
    private val loadArticlesUseCase: LoadArticlesUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val articleScrollPositionUseCase: ArticleScrollPositionUseCase,
    private val articleOfflineContentUseCase: ArticleOfflineContentUseCase,
) : BaseViewModel() {

    private val modeMutable: MutableStateFlow<Mode> = MutableStateFlow(Mode.BOOKMARKED)
    private val notFullyReadArticlesMutable: MutableStateFlow<List<UiArticleElement>> = MutableStateFlow(emptyList())
    private val bookmarkedArticlesMutable: MutableStateFlow<List<UiArticleElement>> = MutableStateFlow(emptyList())

    val tabs: Flow<List<Tab>> = combine(
        notFullyReadArticlesMutable,
        bookmarkedArticlesMutable,
        modeMutable
    ) { notFullyReadArticles, _, mode ->
        if (notFullyReadArticles.isEmpty()) {
            emptyList()
        } else {
            listOf(
                Tab(
                    title = context.getString(R.string.screen_favorites),
                    selected = mode == Mode.BOOKMARKED,
                    mode = Mode.BOOKMARKED,
                ),
                Tab(
                    title = context.getString(R.string.bookmarks_not_fully_read),
                    selected = mode == Mode.NOT_FULLY_READ,
                    mode = Mode.NOT_FULLY_READ,
                )
            )
        }
    }

    val articles: Flow<List<UiArticleElement>> = combine(
        notFullyReadArticlesMutable,
        bookmarkedArticlesMutable,
        modeMutable
    ) { notFullyReadArticles, bookmarkedArticles, mode ->
        when (mode) {
            Mode.NOT_FULLY_READ -> {
                if (notFullyReadArticles.isEmpty()) {
                    bookmarkedArticles
                } else {
                    notFullyReadArticles.map { article ->
                        val bookmarkedIds = bookmarkedArticles.map { it.article.id }.toHashSet()
                        article.updateArticle {
                            copy(bookmarked = id in bookmarkedIds)
                        }
                    }
                }
            }
            Mode.BOOKMARKED -> bookmarkedArticles
        }
    }

    fun load() {
        launch(IO) {
            combine(
                articleScrollPositionUseCase.observeNotFullyReadArticles(),
                articleOfflineContentUseCase.observe(),
            ) { articles, offline ->
                articles.map { domain ->
                    UiArticleElement.from(domain.toUi(false), offline)
                }
            }
                .collectTo(notFullyReadArticlesMutable)
        }

        launch(IO) {
            combine(
                loadArticlesUseCase.loadBookmarked(),
                articleOfflineContentUseCase.observe(),
            ) { bookmarked, offline ->
                bookmarked.map { domain ->
                    UiArticleElement.from(domain.toUi(false, bookmarked = true), offline)
                }
            }
                .collectTo(bookmarkedArticlesMutable)
        }
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }

    fun onTabClick(tab: Tab) {
        modeMutable.value = tab.mode
    }

    class Tab(
        val title: String,
        val selected: Boolean,
        val mode: Mode
    )

    enum class Mode {
        NOT_FULLY_READ,
        BOOKMARKED,
    }
}
