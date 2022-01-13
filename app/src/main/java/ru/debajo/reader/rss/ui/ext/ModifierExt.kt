package ru.debajo.reader.rss.ui.ext

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.optionalClickable(onClick: (() -> Unit)?): Modifier {
    if (onClick == null) {
        return this
    }
    return clickable { onClick() }
}

fun Modifier.hapticClickable(
    hapticFeedbackType: HapticFeedbackType = HapticFeedbackType.TextHandleMove,
    onClick: (() -> Unit)
): Modifier {
    return composed {
        val hapticFeedback = LocalHapticFeedback.current
        clickable {
            hapticFeedback.performHapticFeedback(hapticFeedbackType)
            onClick()
        }
    }
}

fun Modifier.animatedHeight(height: Dp): Modifier {
    return composed {
        var previousValue by remember { mutableStateOf<Float?>(null) }
        val animatable = remember { Animatable(height.value) }
        LaunchedEffect(key1 = height, block = {
            if (previousValue == null || previousValue != height.value) {
                previousValue = height.value
                animatable.animateTo(height.value)
            }
        })
        height(animatable.value.dp)
    }
}