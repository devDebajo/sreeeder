package ru.debajo.reader.rss.ui.add

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.data.converter.toUi
import ru.debajo.reader.rss.domain.channel.ChannelsRepository
import ru.debajo.reader.rss.ui.channels.model.UiChannel

class AddChannelScreenViewModel(
    private val channelsRepository: ChannelsRepository,
) : BaseViewModel() {

    private val textMutable: MutableStateFlow<String> = MutableStateFlow("")
    private val currentChannelMutable: MutableStateFlow<UiChannel?> = MutableStateFlow(null)

    val text: StateFlow<String> = textMutable
    val currentChannel: StateFlow<UiChannel?> = currentChannelMutable

    fun onTextChanged(text: String) {
        textMutable.value = text
    }

    fun onLoadClick() {
        launch(IO) {
            val channel = runCatching { channelsRepository.getChannel(text.value) }.getOrNull()
            currentChannelMutable.emit(channel?.toUi())
        }
    }

    fun reset() {
        textMutable.value = ""
        currentChannelMutable.value = null
    }
}
