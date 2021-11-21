package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["cacheGroup", "dataId"])
data class DbCacheMarker(
    @ColumnInfo(name = "cacheGroup")
    val cacheGroup: String,

    @ColumnInfo(name = "dataId")
    val dataId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
)
