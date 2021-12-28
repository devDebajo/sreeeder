package ru.debajo.reader.rss.ui.bookmarks

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle

class BookmarksListViewModel(
    private val loadArticlesUseCase: LoadArticlesUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<UiArticle>> = MutableStateFlow(emptyList())
    val articles: StateFlow<List<UiArticle>> = articlesMutable

    fun load() {
        launch(IO) {
            loadArticlesUseCase.loadBookmarked()
                .map { domain -> domain.map { entry -> entry.article.toUi(entry.channel?.toUi(), false) } }
                .collectTo(articlesMutable)
        }
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }
}
