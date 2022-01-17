package ru.debajo.reader.rss.ui.ext

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.loadImage

suspend fun Bitmap.generatePalette(): Palette? {
    return withContext(Default) {
        runCatching { Palette.from(this@generatePalette).generate() }
            .getOrNull()
    }
}

@ColorInt
fun Palette.getDominantColor(): Int? = dominantSwatch?.rgb

suspend fun String.loadDominantColorFromImage(context: Context): Int? {
    return loadImage(context, this)?.generatePalette()?.getDominantColor()
}
