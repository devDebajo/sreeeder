package ru.debajo.reader.rss.ui.main

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.domain.article.NewArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class MainViewModel(
    private val newArticlesUseCase: NewArticlesUseCase
) : BaseViewModel() {

    private val selectedTabMutable: MutableStateFlow<Int> = MutableStateFlow(0)
    private val feedBadgeCountMutable: MutableStateFlow<Int> = MutableStateFlow(0)

    val selectedTab: StateFlow<Int> = selectedTabMutable
    val feedBadgeCount: StateFlow<Int> = feedBadgeCountMutable

    init {
        launch {
            newArticlesUseCase.observeNewCount()
                .flowOn(IO)
                .collectTo(feedBadgeCountMutable)
        }
    }

    fun updateSelectedTab(index: Int) {
        selectedTabMutable.value = index
    }
}
