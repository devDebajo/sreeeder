package ru.debajo.reader.rss.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.ui.common.list.SreeederListState
import ru.debajo.reader.rss.ui.common.list.rememberSreeederListState
import ru.debajo.reader.rss.ui.list.ScrollController.Scroller

class ScrollController(coroutineScope: CoroutineScope) : CoroutineScope by coroutineScope {

    private val states: HashMap<String, Scroller> = HashMap()

    fun scrollToTop(area: String) {
        launch {
            states[area]?.scrollToItem(0)
        }
    }

    @Composable
    fun rememberLazyListState(
        area: String,
        initialFirstVisibleItemIndex: Int = 0,
        initialFirstVisibleItemScrollOffset: Int = 0
    ): SreeederListState {
        val state = rememberSreeederListState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )

        remember(area, state) {
            Scroller { index -> state.animateScrollToItem(index) }
                .also { states[area] = it }
        }

        return state
    }

    private fun interface Scroller {
        suspend fun scrollToItem(index: Int)
    }
}

@Composable
fun rememberScrollController(): ScrollController {
    val scope = rememberCoroutineScope()
    return remember(scope) { ScrollController(scope) }
}