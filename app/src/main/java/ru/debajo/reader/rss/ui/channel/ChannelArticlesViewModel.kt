package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ViewedArticlesRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class ChannelArticlesViewModel(
    private val subscriptionsRepository: ChannelsSubscriptionsRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val loadArticlesUseCase: LoadArticlesUseCase,
    private val analytics: Analytics,
    private val viewedArticlesRepository: ViewedArticlesRepository
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<UiArticle>> = MutableStateFlow(emptyList())
    private val isSubscribedMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val articles: StateFlow<List<UiArticle>> = articlesMutable
    val isSubscribed: StateFlow<Boolean> = isSubscribedMutable

    fun load(channel: UiChannel) {
        launch {
            loadArticlesUseCase.load(channel.toDomain())
                .map { domain -> domain.map { entry -> entry.article.toUi(null) } }
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

    fun onShare() {
        analytics.onShareChannel()
    }

    fun onArticleViewed(article: UiArticle) {
        launch {
            viewedArticlesRepository.onViewed(article.id)
        }
    }
}
