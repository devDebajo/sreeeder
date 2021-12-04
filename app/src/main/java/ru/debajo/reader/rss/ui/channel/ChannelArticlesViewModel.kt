package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticlesRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class ChannelArticlesViewModel(
    private val articlesRepository: ArticlesRepository,
    private val subscriptionsRepository: ChannelsSubscriptionsRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<UiArticle>> = MutableStateFlow(emptyList())
    private val isSubscribedMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val articles: StateFlow<List<UiArticle>> = articlesMutable
    val isSubscribed: StateFlow<Boolean> = isSubscribedMutable

    fun load(channel: UiChannel) {
        launch {
            articlesRepository.getArticles(channel.url.toDomain())
                .map { it.toUiList() }
                .collectTo(articlesMutable)
        }
        launch {
            subscriptionsRepository.isSubscribed(channel.url.toDomain())
                .flowOn(IO)
                .collectTo(isSubscribedMutable)
        }
    }

    fun onSubscribeClick(channel: UiChannel) {
        launch {
            subscriptionsRepository.toggle(channel.url.toDomain())
        }
    }

    fun onFavoriteClick(article: UiArticle) {
        launch {
            articleBookmarksRepository.toggle(article.id)
        }
    }
}
