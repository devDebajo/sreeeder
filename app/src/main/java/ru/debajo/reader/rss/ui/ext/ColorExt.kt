package ru.debajo.reader.rss.ui.ext

import android.os.Build
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color

val Color.colorInt: Int
    @ColorInt
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.graphics.Color.argb(alpha, red, green, blue)
        } else {
            (alpha * 255.0f + 0.5f).toInt() shl 24 or
                    ((red * 255.0f + 0.5f).toInt() shl 16) or
                    ((green * 255.0f + 0.5f).toInt() shl 8) or
                    (blue * 255.0f + 0.5f).toInt()
        }
    }