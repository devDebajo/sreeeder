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
    val descriptionHtml: String?,
    val contentHtml: String?,
    val timestamp: DateTime?
) : Parcelable
