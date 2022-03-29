package ru.debajo.reader.rss.ui.ext

import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import kotlin.math.ln

typealias AndroidColor = android.graphics.Color

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

val Int.composeColor: Color
    get() = Color(this)

fun Int.getColorRoles(context: Context): ColorRoles {
    return MaterialColors.getColorRoles(context, this)
}

fun Color.getColorRoles(context: Context): ColorRoles = colorInt.getColorRoles(context)

fun getNavigationColor(colorScheme: ColorScheme): Color {
    val alpha = ((4.5f * ln(3.dp.value + 1)) + 2f) / 100f
    return colorScheme.surfaceTint.copy(alpha = alpha).compositeOver(colorScheme.surface)
}