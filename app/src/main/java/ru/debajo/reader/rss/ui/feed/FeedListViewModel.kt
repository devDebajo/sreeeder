package ru.debajo.reader.rss.ui.feed

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.feed.FeedListUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

@FlowPreview
@ExperimentalCoroutinesApi
class FeedListViewModel(
    private val useCase: FeedListUseCase,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<Pair<UiArticle, UiChannel>>> = MutableStateFlow(emptyList())
    val articles: StateFlow<List<Pair<UiArticle, UiChannel>>> = articlesMutable

    fun load() {
        launch(IO) {
            useCase()
                .map { domain -> domain.map { entry -> entry.article.toUi() to entry.channel.toUi() } }
                .collectTo(articlesMutable)
        }
    }
}
