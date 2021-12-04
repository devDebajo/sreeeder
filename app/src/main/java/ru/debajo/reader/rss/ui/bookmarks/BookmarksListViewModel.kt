package ru.debajo.reader.rss.ui.bookmarks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.channel.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

@FlowPreview
@ExperimentalCoroutinesApi
class BookmarksListViewModel(
    private val loadArticlesUseCase: LoadArticlesUseCase,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<Pair<UiArticle, UiChannel?>>> = MutableStateFlow(emptyList())
    val articles: StateFlow<List<Pair<UiArticle, UiChannel?>>> = articlesMutable

    fun load() {
        launch(Dispatchers.IO) {
            loadArticlesUseCase.loadBookmarked()
                .map { domain -> domain.map { entry -> entry.article.toUi() to entry.channel?.toUi() } }
                .collectTo(articlesMutable)
        }
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }
}
