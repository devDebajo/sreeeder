package ru.debajo.reader.rss.ui.main

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.preferences.ShowNavigationTitlesPreference
import ru.debajo.reader.rss.data.preferences.base.PreferenceObserver
import ru.debajo.reader.rss.domain.article.NewArticlesUseCase
import ru.debajo.reader.rss.ext.collectTo
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class MainViewModel(
    private val newArticlesUseCase: NewArticlesUseCase,
    private val preferenceObserver: PreferenceObserver,
    private val showNavigationTitlesPreference: ShowNavigationTitlesPreference,
) : BaseViewModel() {

    private val selectedTabMutable: MutableStateFlow<Int> = MutableStateFlow(0)
    private val feedBadgeCountMutable: MutableStateFlow<Int> = MutableStateFlow(0)
    private val showNavigationTitlesMutable: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val selectedTab: StateFlow<Int> = selectedTabMutable
    val feedBadgeCount: StateFlow<Int> = feedBadgeCountMutable
    val showNavigationTitles: StateFlow<Boolean> = showNavigationTitlesMutable

    init {
        launch {
            newArticlesUseCase.observeNewCount()
                .flowOn(IO)
                .collectTo(feedBadgeCountMutable)
        }
        launch {
            showNavigationTitlesMutable.value = showNavigationTitlesPreference.get()
            preferenceObserver.observe(showNavigationTitlesPreference)
                .collectTo(showNavigationTitlesMutable)
        }
    }

    fun updateSelectedTab(index: Int) {
        selectedTabMutable.value = index
    }
}
