package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbArticleScrollPosition(
    @PrimaryKey
    @ColumnInfo(name = "articleId")
    val articleId: String,

    @ColumnInfo(name = "scroll")
    val scroll: Int,
)
