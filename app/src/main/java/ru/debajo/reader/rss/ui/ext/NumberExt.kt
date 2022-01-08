package ru.debajo.reader.rss.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.pxToDp(): Dp = toFloat().pxToDp()

@Composable
fun Float.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Float.toFinite(default: Float = 0f): Float = if (isFinite()) this else default
