package ru.debajo.reader.rss.ui.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.feed.FeedListViewModel
import ru.debajo.reader.rss.ui.feed.model.FeedListState

@Composable
fun MainScreenTopBarActions(state: FeedListState, viewModel: FeedListViewModel) {
    Text(stringResource(R.string.feed_only_new), fontSize = 10.sp)
    Checkbox(checked = state.showOnlyNewArticles, onCheckedChange = {
        viewModel.onOnlyNewArticlesClick(it)
    })

    var menuVisible by remember { mutableStateOf(false) }
    var iconPosition by remember { mutableStateOf(0f) }
    IconButton(
        modifier = Modifier.onGloballyPositioned { coordinates ->
            iconPosition = coordinates.boundsInRoot().left
        },
        onClick = { menuVisible = true }
    ) {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = null
        )
    }
    val leftOffset = with(LocalDensity.current) { iconPosition.toDp() }
    DropdownMenu(
        offset = DpOffset(x = leftOffset, y = 0.dp),
        modifier = Modifier.requiredSizeIn(minWidth = 200.dp),
        expanded = menuVisible,
        onDismissRequest = { menuVisible = false },
    ) {
        DropdownMenuItem(
            onClick = {
                menuVisible = false
                viewModel.onMarkAllAsRead()
            },
        ) {
            Icon(
                Icons.Rounded.Visibility,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.feed_mark_all_as_read))
        }
    }
}