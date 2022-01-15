package ru.debajo.reader.rss.ui.ext

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PaddingValues.addPadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return remember(layoutDirection, start, top, end, bottom) {
        PaddingValues(
            start = start + calculateStartPadding(layoutDirection),
            top = top + calculateTopPadding(),
            end = end + calculateEndPadding(layoutDirection),
            bottom = bottom + calculateBottomPadding(),
        )
    }
}
