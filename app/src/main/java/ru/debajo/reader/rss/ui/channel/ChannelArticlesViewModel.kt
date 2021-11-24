package ru.debajo.reader.rss.ui.channel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import timber.log.Timber


class ChannelArticlesViewModel(
    private val channelsRepository: ChannelsRepository,
) : BaseViewModel() {

    private val articlesMutable: MutableStateFlow<Document?> = MutableStateFlow(null)
    val articles: StateFlow<Document?> = articlesMutable

    fun load(channel: UiChannel) {
        launch {
            Timber.d("yopta start load")
            val articles = channelsRepository.getArticles(channel.url).toUiList()
            Timber.d("yopta loaded")
            articlesMutable.value = articles.firstOrNull()?.content
        }
    }
}
