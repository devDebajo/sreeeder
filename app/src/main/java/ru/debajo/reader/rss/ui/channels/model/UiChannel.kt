package ru.debajo.reader.rss.ui.channels.model

import androidx.compose.runtime.Stable

@Stable
data class UiChannel(
    val url: String,
    val name: String,
    val image: String?,
    val description: String?,
)
