package ru.debajo.reader.rss.ui.common

import androidx.compose.animation.core.Easing
import kotlin.math.pow

fun decelerateEasing(factor: Double = 1.0): Easing = Easing { input ->
    if (factor == 1.0) {
        (1.0 - (1.0 - input) * (1.0 - input))
    } else {
        (1.0 - (1.0 - input).pow(2 * factor))
    }.toFloat()
}
