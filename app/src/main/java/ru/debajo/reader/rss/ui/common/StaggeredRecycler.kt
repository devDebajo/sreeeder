package ru.debajo.reader.rss.ui.common

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

@Composable
fun <T> StaggeredRecycler(
    modifier: Modifier = Modifier,
    spanCount: Int,
    data: List<T>,
    keyEquality: ((old: T, new: T) -> Boolean)? = null,
    contentEquality: (old: T, new: T) -> Boolean = { old, new -> old == new },
    content: @Composable (T) -> Unit,
) {
    val adapter = remember { ComposeAdapter(keyEquality, contentEquality) }
    val latestContent by rememberUpdatedState(newValue = content)
    LaunchedEffect(key1 = adapter, block = {
        snapshotFlow { latestContent }.collect {
            adapter.content = content
        }
    })
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RecyclerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )

                this.adapter = adapter
                layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            }
        },
        update = { adapter.items = data }
    )
}
