package ru.debajo.reader.rss.ui.main

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class MainViewModel : BaseViewModel() {

    private val selectedTabMutable: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = selectedTabMutable

    fun updateSelectedTab(index: Int) {
        selectedTabMutable.value = index
    }
}
