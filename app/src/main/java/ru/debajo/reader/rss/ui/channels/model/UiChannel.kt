package ru.debajo.reader.rss.ui.channels.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class UiChannel(
    val url: String,
    val name: String,
    val image: String?,
    val description: String?,
) : Parcelable
