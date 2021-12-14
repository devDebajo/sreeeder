package ru.debajo.reader.rss.ui.feed.model

import ru.debajo.reader.rss.ui.article.model.UiArticle

data class FeedListState(
    val tabs: List<UiFeedTab> = emptyList(),
    val selectedTab: Int = 0,
    val dataSet: Map<String, List<UiArticle>> = emptyMap(),
) {
    val articles: List<UiArticle>
        get() {
            if (dataSet.isEmpty()) {
                return emptyList()
            }
            if (dataSet.size == 1) {
                return dataSet.entries.first().value
            }
            // ----------------------------------------------invalid state
            val selectedTab = tabs.getOrNull(selectedTab) ?: return dataSet.entries.first().value
            return dataSet[selectedTab.code].orEmpty()
        }

    fun isSelected(tab: UiFeedTab): Boolean {
        return tabs.getOrNull(selectedTab)?.code == tab.code
    }
}
