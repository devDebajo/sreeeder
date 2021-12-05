package ru.debajo.reader.rss.ui.add

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.data.remote.RssLoadDbManager
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.domain.model.DomainChannelUrl
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ext.withLeading
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class AddChannelScreenViewModel(
    private val channelsRepository: ChannelsRepository,
    private val rssLoadDbManager: RssLoadDbManager
) : BaseViewModel() {

    private val textMutable: MutableStateFlow<String> = MutableStateFlow("")
    private val currentChannelMutable: MutableStateFlow<AddChannelScreenState> = MutableStateFlow(AddChannelScreenState.Idle)
    private var currentJob: Job? = null

    val text: StateFlow<String> = textMutable
    val state: StateFlow<AddChannelScreenState> = currentChannelMutable

    fun onTextChanged(text: String) {
        textMutable.value = text
    }

    fun onLoadClick() {
        val url = DomainChannelUrl(text.value)
        currentJob?.cancel()
        currentJob = launch(IO) {
            combine(
                channelsRepository.getChannel(url).map { it?.toUi() }.withLeading(null),
                rssLoadDbManager.refreshChannel(url, force = true),
            ) { channel, loadingState ->
                when (loadingState) {
                    is RssLoadDbManager.ChannelLoadingState.Error -> AddChannelScreenState.Error(loadingState.throwable)
                    is RssLoadDbManager.ChannelLoadingState.Refreshing -> AddChannelScreenState.Loading
                    is RssLoadDbManager.ChannelLoadingState.UpToDate -> {
                        if (channel == null) {
                            AddChannelScreenState.Loading
                        } else {
                            AddChannelScreenState.Loaded(channel)
                        }
                    }
                }
            } collectTo currentChannelMutable
        }
    }
}
