package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbChannel(
    @PrimaryKey
    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "imageDominantColor")
    val imageDominantColor: Int?,

    @ColumnInfo(name = "description")
    val description: String?,
)
