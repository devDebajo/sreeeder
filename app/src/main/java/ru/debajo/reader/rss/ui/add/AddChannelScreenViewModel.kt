package ru.debajo.reader.rss.ui.add

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.converter.toUiList
import ru.debajo.reader.rss.domain.search.SearchChannelsUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.arch.SingleLiveEvent

class AddChannelScreenViewModel(
    private val searchChannelsUseCase: SearchChannelsUseCase,
) : BaseViewModel() {

    private val textMutable: MutableStateFlow<String> = MutableStateFlow("")
    private val currentChannelMutable: MutableStateFlow<AddChannelScreenState> = MutableStateFlow(AddChannelScreenState.Idle)
    private var currentJob: Job? = null
    private val requestFocusMutable: SingleLiveEvent<Unit> = SingleLiveEvent()
    private var focusRequested: Boolean = false
    val text: StateFlow<String> = textMutable
    val state: StateFlow<AddChannelScreenState> = currentChannelMutable
    val requestFocus: LiveData<Unit> = requestFocusMutable

    fun requestFocus() {
        if (!focusRequested) {
            launch {
                delay(500)
                requestFocusMutable.value = Unit
                focusRequested = true
            }
        }
    }

    fun onTextChanged(text: String) {
        textMutable.value = text
    }

    fun onLoadClick() {
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

    fun clearQuery() {
        textMutable.value = ""
        currentChannelMutable.value = AddChannelScreenState.Idle
    }
}
