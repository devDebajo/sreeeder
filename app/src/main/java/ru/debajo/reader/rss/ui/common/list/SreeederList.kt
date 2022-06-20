package ru.debajo.reader.rss.ui.common.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.staggeredlazycolumn.StaggeredLazyColumn
import ru.debajo.staggeredlazycolumn.StaggeredLazyColumnCells
import ru.debajo.staggeredlazycolumn.state.StaggeredLazyColumnScrollState

@Composable
fun rememberSreeederListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
): SreeederListState {
    return rememberSaveable(saver = SreeederListState.Saver) {
        SreeederListState(
            initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset
        )
    }
}

@Stable
class SreeederListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0,
) {

    private val staggeredEnabled: Boolean
        get() = BuildConfig.STAGGERED_LIST_ENABLED

    val lazyListState: LazyListState by lazy {
        LazyListState(initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset)
    }

    val staggeredLazyListState: StaggeredLazyColumnScrollState by lazy {
        StaggeredLazyColumnScrollState(initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset)
    }

    fun canScroll(direction: ScrollDirection): Boolean {
        return if (staggeredEnabled) {
            staggeredLazyListState.canScroll(
                when (direction) {
                    ScrollDirection.DOWN -> StaggeredLazyColumnScrollState.ScrollDirection.DOWN
                    ScrollDirection.UP -> StaggeredLazyColumnScrollState.ScrollDirection.UP
                }
            )
        } else {
            when (direction) {
                ScrollDirection.DOWN -> lazyListState.firstVisibleItemIndex + lazyListState.firstVisibleItemScrollOffset > 0
                ScrollDirection.UP -> {
                    val can = lazyListState.dispatchRawDelta(1f) > 0f
                    lazyListState.dispatchRawDelta(-1f)
                    can
                }
            }
        }
    }

    val firstVisibleItemIndex: Int
        get() {
            return if (staggeredEnabled) {
                staggeredLazyListState.firstVisibleItemIndex
            } else {
                lazyListState.firstVisibleItemIndex
            }
        }

    val firstVisibleItemScrollOffset: Int
        get() {
            return if (staggeredEnabled) {
                staggeredLazyListState.firstVisibleItemScrollOffset
            } else {
                lazyListState.firstVisibleItemScrollOffset
            }
        }

    val isScrollInProgress: Boolean
        get() {
            return if (staggeredEnabled) {
                staggeredLazyListState.isScrollInProgress
            } else {
                lazyListState.isScrollInProgress
            }
        }

    fun observeScrollDirection(): Flow<ScrollDirection?> {
        return if (staggeredEnabled) {
            snapshotFlow {
                when (staggeredLazyListState.scrollDirection) {
                    StaggeredLazyColumnScrollState.ScrollDirection.DOWN -> ScrollDirection.DOWN
                    StaggeredLazyColumnScrollState.ScrollDirection.UP -> ScrollDirection.UP
                    null -> null
                }
            }
        } else {
            var previousItem = -1
            snapshotFlow<ScrollDirection?> {
                val direction = if (lazyListState.firstVisibleItemIndex > previousItem) {
                    ScrollDirection.UP
                } else {
                    ScrollDirection.DOWN
                }
                previousItem = lazyListState.firstVisibleItemIndex
                direction
            }.onStart { emit(null) }
        }
    }

    suspend fun animateScrollToItem(index: Int) {
        if (staggeredEnabled) {
            staggeredLazyListState.animateScrollToItem(index)
        } else {
            lazyListState.animateScrollToItem(index)
        }
    }

    enum class ScrollDirection { DOWN, UP }

    companion object {
        val Saver: Saver<SreeederListState, *> = Saver(
            save = { listOf(it.firstVisibleItemIndex, it.firstVisibleItemScrollOffset) },
            restore = { SreeederListState(it[0], it[1]) }
        )
    }
}

@Composable
fun SreeederList(
    modifier: Modifier = Modifier,
    state: SreeederListState = rememberSreeederListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemCount: Int,
    verticalSpacing: Dp = 0.dp,
    columns: Int = 1,
    header: @Composable (() -> Unit)? = null,
    key: ((index: Int) -> Any)? = null,
    contentType: (index: Int) -> Any? = { null },
    itemFactory: @Composable (Int) -> Unit,
) {
    if (!BuildConfig.STAGGERED_LIST_ENABLED) {
        LazyColumn(
            modifier = modifier,
            state = state.lazyListState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            content = {
                if (header != null) {
                    item {
                        header()
                    }
                }
                items(
                    count = itemCount,
                    key = key,
                    contentType = contentType,
                    itemContent = { itemFactory(it) }
                )
            }
        )
    } else {
        StaggeredLazyColumn(
            modifier = modifier,
            state = state.staggeredLazyListState,
            contentPadding = contentPadding,
            verticalSpacing = verticalSpacing,
            columns = StaggeredLazyColumnCells.Fixed(columns),
            content = {
                if (header != null) {
                    item {
                        header()
                    }
                }
                items(
                    count = itemCount,
                    key = key,
                    contentType = contentType,
                    itemContent = { itemFactory(it) }
                )
            }
        )
    }
}
