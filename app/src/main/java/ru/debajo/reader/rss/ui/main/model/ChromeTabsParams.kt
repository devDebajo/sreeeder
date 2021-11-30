package ru.debajo.reader.rss.ui.main.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChromeTabsParams(
    val url: String,
    @ColorInt
    val toolbarColor: Int? = null,
) : Parcelable

fun String.toChromeTabsParams(toolbarColor: Int? = null): ChromeTabsParams {
    return ChromeTabsParams(this, toolbarColor)
}