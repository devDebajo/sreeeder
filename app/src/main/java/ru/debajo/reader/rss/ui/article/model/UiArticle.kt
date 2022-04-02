package ru.debajo.reader.rss.ui.article.model

import android.os.Parcelable
import androidx.annotation.IntRange
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
    @IntRange(from = 0, to = 100)
    val readPercents: Int,
) : Parcelable {
    companion object {
        fun fromUrl(url: String): UiArticle {
            return UiArticle(
                id = url,
                author = null,
                title = "",
                image = null,
                url = url,
                rawHtmlContent = null,
                bookmarked = false,
                isNew = false,
                timestamp = null,
                channelImage = null,
                channelName = null,
                categories = emptyList(),
                readPercents = 0
            )
        }
    }
}

