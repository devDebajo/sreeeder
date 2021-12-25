package ru.debajo.reader.rss.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.debajo.reader.rss.ui.ext.optionalClickable

private const val ROUNDED_CORNERS = 20f

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    corners: AppCardCorners = AppCardCorners.ALL,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val topAnimatable = remember { Animatable(corners.topCorner) }
    val bottomAnimatable = remember { Animatable(corners.bottomCorner) }

    LaunchedEffect(key1 = corners, block = {
        if (topAnimatable.value != corners.topCorner) {
            topAnimatable.animateTo(corners.topCorner)
        }
        if (bottomAnimatable.value != corners.bottomCorner) {
            bottomAnimatable.animateTo(corners.bottomCorner)
        }
    })

    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topEnd = topAnimatable.value.dp,
                    topStart = topAnimatable.value.dp,
                    bottomStart = bottomAnimatable.value.dp,
                    bottomEnd = bottomAnimatable.value.dp,
                )
            )
            .background(bgColor)
            .optionalClickable(onClick)
            .then(modifier),
    ) {
        content()
    }
}

private val AppCardCorners.topCorner: Float
    get() {
        return when (this) {
            AppCardCorners.TOP, AppCardCorners.ALL -> ROUNDED_CORNERS
            AppCardCorners.BOTTOM, AppCardCorners.NONE -> 0f
        }
    }

private val AppCardCorners.bottomCorner: Float
    get() {
        return when (this) {
            AppCardCorners.TOP, AppCardCorners.NONE -> 0f
            AppCardCorners.ALL, AppCardCorners.BOTTOM -> ROUNDED_CORNERS
        }
    }

enum class AppCardCorners {
    TOP,
    BOTTOM,
    NONE,
    ALL
}