package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbCacheMarker(
    @PrimaryKey
    @ColumnInfo(name = "cacheGroup")
    val cacheGroup: String,

    @ColumnInfo(name = "dataId")
    val dataId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
)
