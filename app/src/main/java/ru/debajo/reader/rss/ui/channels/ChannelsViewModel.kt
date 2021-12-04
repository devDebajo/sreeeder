package ru.debajo.reader.rss.ui.channels

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.channel.ChannelsSubscriptionsUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.channels.model.UiChannel

@ExperimentalCoroutinesApi
class ChannelsViewModel(
    private val useCase: ChannelsSubscriptionsUseCase,
) : BaseViewModel() {

    val channels: MutableStateFlow<List<UiChannel>> = MutableStateFlow(emptyList())

    fun load() {
        launch(IO) {
            useCase.observe()
                .map { list -> list.toUiList() }
                .collectTo(channels)
        }
    }
}
