package ru.debajo.reader.rss.ui.add

import ru.debajo.reader.rss.ui.channels.model.UiChannel

sealed interface AddChannelScreenState {
    object Idle : AddChannelScreenState
    object Loading : AddChannelScreenState
    object NotFound : AddChannelScreenState
    class Loaded(val channels: List<UiChannel>) : AddChannelScreenState
}
