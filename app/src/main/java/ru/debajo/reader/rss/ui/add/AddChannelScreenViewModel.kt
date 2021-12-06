package ru.debajo.reader.rss.ui.add

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.search.SearchChannelsUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class AddChannelScreenViewModel(
    private val searchChannelsUseCase: SearchChannelsUseCase,
    private val analytics: Analytics,
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
        analytics.onLoadChannel()
        currentJob?.cancel()
        currentJob = launch(IO) {
            searchChannelsUseCase.search(text.value)
                .flowOn(IO)
                .map { channels ->
                    if (channels.isEmpty()) {
                        AddChannelScreenState.Loading
                    } else {
                        AddChannelScreenState.Loaded(channels.toUiList())
                    }
                }
                .onStart { emit(AddChannelScreenState.Loading) }
                .onCompletion {
                    if (currentChannelMutable.value is AddChannelScreenState.Loading) {
                        emit(AddChannelScreenState.NotFound)
                    }
                }
                .collectTo(currentChannelMutable)
        }
    }
}
