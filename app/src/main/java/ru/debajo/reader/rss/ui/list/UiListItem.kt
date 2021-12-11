package ru.debajo.reader.rss.ui.list

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable

interface UiListItem {
    val id: String
}

inline fun <T : UiListItem> LazyListScope.uiListItems(
    items: List<T>,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit
) {
    items(
        count = items.size,
        key = { index -> items[index].id },
        itemContent = { itemContent(items[it]) }
    )
}
