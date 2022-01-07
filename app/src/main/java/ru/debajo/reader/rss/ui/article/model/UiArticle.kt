package ru.debajo.reader.rss.ui.article.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime

@Parcelize
data class UiArticle(
    val id: String,
    val author: String?,
    val title: String,
    val image: String?,
    val url: String,
    val rawHtmlContent: String?,
    val bookmarked: Boolean,
    val isNew: Boolean,
    val timestamp: DateTime?,
    val channelImage: String?,
    val channelName: String?,
    val categories: List<String>,
) : Parcelable
