package ru.debajo.reader.rss.ui.article.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import kotlin.random.Random

@Parcelize
data class UiArticle(
    val id: String,
    val author: String?,
    val title: String,
    val image: String?,
    val url: String,
    val bookmarked: Boolean,
    val isNew: Boolean = Random.nextBoolean(),
    val timestamp: DateTime?,
    val channelImage: String?,
    val channelName: String?,
    val categories: List<String>,
) : Parcelable
