package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DbNewArticle(
    @PrimaryKey
    @ColumnInfo(name = "articleId")
    val articleId: String,

    @ColumnInfo(name = "channelUrl")
    val channelUrl: String,
)