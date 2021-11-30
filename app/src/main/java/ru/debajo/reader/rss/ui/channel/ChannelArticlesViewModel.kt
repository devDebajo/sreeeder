package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsRepository
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class ChannelArticlesViewModel(
    private val channelsRepository: ChannelsRepository,
    private val subscriptionsRepository: ChannelsSubscriptionsRepository
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<UiArticle>> = MutableStateFlow(emptyList())
    private val isSubscribedMutable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val articles: StateFlow<List<UiArticle>> = articlesMutable
    val isSubscribed: StateFlow<Boolean> = isSubscribedMutable

    fun load(channel: UiChannel) {
        launch {
            channelsRepository.getArticles(channel.url)
                .map { it.toUiList() }
                .collectTo(articlesMutable)
        }
        launch {
            isSubscribedMutable.value = subscriptionsRepository.isSubscribed(channel.url)
        }
    }

    fun onSubscribeClick(channel: UiChannel) {
        launch {
            if (subscriptionsRepository.isSubscribed(channel.url)) {
                subscriptionsRepository.remove(channel.url)
                isSubscribedMutable.value = false
            } else {
                subscriptionsRepository.add(channel.url)
                isSubscribedMutable.value = true
            }
        }
    }
}
