package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.debajo.reader.rss.domain.model.DomainArticle

@Entity
data class DbArticle(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "channelUrl")
    val channelUrl: String,

    @ColumnInfo(name = "channelName")
    val channelName: String,

    @ColumnInfo(name = "channelImage")
    val channelImage: String?,

    @ColumnInfo(name = "author")
    val author: String?,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "contentHtml")
    val contentHtml: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: DbDateTime?,

    @ColumnInfo(name = "categories")
    val categories: List<String>,
)

fun DomainArticle.toDb(): DbArticle {
    return DbArticle(
        id = id,
        channelUrl = channelUrl.url,
        channelName = channelName,
        channelImage = channelImage,
        author = author,
        title = title,
        image = image,
        url = url,
        contentHtml = contentHtml,
        timestamp = timestamp?.toDb(),
        categories = categories,
    )
}
