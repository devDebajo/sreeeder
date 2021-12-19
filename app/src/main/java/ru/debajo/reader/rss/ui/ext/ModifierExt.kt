package ru.debajo.reader.rss.ui.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.optionalClickable(onClick: (() -> Unit)?): Modifier {
    if (onClick == null) {
        return this
    }
    return clickable { onClick() }
}