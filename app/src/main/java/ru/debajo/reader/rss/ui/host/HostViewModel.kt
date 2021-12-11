package ru.debajo.reader.rss.ui.host

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.db.RssLoadDbManager
import ru.debajo.reader.rss.ext.ignoreElements
import ru.debajo.reader.rss.ui.arch.BaseViewModel

class HostViewModel(
    private val rssLoadDbManager: RssLoadDbManager
) : BaseViewModel() {

    fun refreshFeed() {
        launch(Dispatchers.IO) {
            rssLoadDbManager.refreshSubscriptions(force = true).ignoreElements()
        }
    }
}
