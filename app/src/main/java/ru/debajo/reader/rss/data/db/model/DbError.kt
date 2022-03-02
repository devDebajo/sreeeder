package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DbError(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "stackTrace")
    val stackTrace: String,

    @ColumnInfo(name = "message")
    val message: String?,

    @ColumnInfo(name = "customMessage")
    val customMessage: String?,

    @ColumnInfo(name = "tag")
    val tag: String?,

    @ColumnInfo(name = "date")
    val date: DbDateTime,

    @ColumnInfo(name = "appVersion")
    val appVersion: String,

    @ColumnInfo(name = "sdkVersion")
    val sdkVersion: Int,

    @ColumnInfo(name = "fatal")
    val fatal: Boolean,
)
