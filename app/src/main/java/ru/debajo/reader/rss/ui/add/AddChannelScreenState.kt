package ru.debajo.reader.rss.ui.add

import ru.debajo.reader.rss.ui.channels.model.UiChannel

sealed interface AddChannelScreenState {
    object Idle : AddChannelScreenState
    object Loading : AddChannelScreenState
    class Error(val throwable: Throwable) : AddChannelScreenState
    class Loaded(val channel: UiChannel) : AddChannelScreenState
}
