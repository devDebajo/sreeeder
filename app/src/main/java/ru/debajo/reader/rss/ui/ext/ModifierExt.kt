package ru.debajo.reader.rss.ui.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

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