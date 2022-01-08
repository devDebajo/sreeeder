package ru.debajo.reader.rss.ui.main.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import ru.debajo.reader.rss.ui.ext.colorInt

@Parcelize
data class ChromeTabsParams(
    val url: String,
    @ColorInt
    val toolbarColor: Int? = null,
) : Parcelable

fun String.toChromeTabsParams(toolbarColor: Color? = null): ChromeTabsParams {
    return ChromeTabsParams(this, toolbarColor?.colorInt)
}