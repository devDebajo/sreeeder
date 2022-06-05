package ru.debajo.reader.rss.ui.ext

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
@Stable
fun PaddingValues.addPadding(
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
): PaddingValues {
    return addPadding(
        top = vertical,
        bottom = vertical,
        start = horizontal,
        end = horizontal,
    )
}

@Composable
@Stable
fun PaddingValues.addPadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return remember(this, layoutDirection, start, top, end, bottom) {
        PaddingValues(
            start = start + calculateStartPadding(layoutDirection),
            top = top + calculateTopPadding(),
            end = end + calculateEndPadding(layoutDirection),
            bottom = bottom + calculateBottomPadding(),
        )
    }
}

@Composable
@Stable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return addPadding(
        start = other.calculateStartPadding(layoutDirection),
        top = other.calculateTopPadding(),
        end = other.calculateEndPadding(layoutDirection),
        bottom = other.calculateBottomPadding(),
    )
}
