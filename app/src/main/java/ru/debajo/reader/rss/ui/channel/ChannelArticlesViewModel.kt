package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class ChannelArticlesViewModel(
    private val channelsRepository: ChannelsRepository,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<List<UiArticle>> = MutableStateFlow(emptyList())
    val articles: StateFlow<List<UiArticle>> = articlesMutable

    fun load(channel: UiChannel) {
        launch {
            val articles = channelsRepository.getArticles(channel.url).toUiList()
            articlesMutable.value = articles
        }
    }
}
