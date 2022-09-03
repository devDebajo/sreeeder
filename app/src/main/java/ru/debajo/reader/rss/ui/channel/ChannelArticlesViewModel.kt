package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toDomain
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.article.ArticleBookmarksRepository
import ru.debajo.reader.rss.domain.article.ArticleOfflineContentUseCase
import ru.debajo.reader.rss.domain.article.ClearArticlesUseCase
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.domain.feed.LoadArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.feed.model.UiArticleElement

class ChannelArticlesViewModel(
    private val subscriptionsRepository: ChannelsSubscriptionsRepository,
    private val articleBookmarksRepository: ArticleBookmarksRepository,
    private val loadArticlesUseCase: LoadArticlesUseCase,
    private val clearArticlesUseCase: ClearArticlesUseCase,
    private val articleOfflineContentUseCase: ArticleOfflineContentUseCase,
) : BaseViewModel() {

    private var channel: UiChannel? = null

    private val articlesMutable: MutableStateFlow<List<UiArticleElement>> = MutableStateFlow(emptyList())
    private val isSubscribedMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val articles: StateFlow<List<UiArticleElement>> = articlesMutable
    val isSubscribed: StateFlow<Boolean> = isSubscribedMutable

    fun load(channel: UiChannel) {
        this.channel = channel
        launch {
            combine(
                loadArticlesUseCase.load(channel.toDomain()),
                articleOfflineContentUseCase.observe()
            ) { articles, offline ->
                articles.map { domain ->
                    UiArticleElement.from(
                        article = domain.toUi(false),
                        map = offline
                    )
                }
            }
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

    override fun onCleared() {
        val channel = channel
        if (channel != null && !isSubscribed.value) {
            launch(IO) {
                clearArticlesUseCase.clear(channel.url)
            }
        }

        super.onCleared()
    }
}


