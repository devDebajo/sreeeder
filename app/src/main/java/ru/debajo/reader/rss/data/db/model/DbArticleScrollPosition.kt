package ru.debajo.reader.rss.data.db.model

import androidx.annotation.IntRange
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbArticleScrollPosition(
    @PrimaryKey
    @ColumnInfo(name = "articleId")
    val articleId: String,

    @ColumnInfo(name = "scroll")
    @IntRange(from = 0, to = 100)
    val scroll: Int,
)
