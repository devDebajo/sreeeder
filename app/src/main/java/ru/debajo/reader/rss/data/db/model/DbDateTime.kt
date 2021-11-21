package ru.debajo.reader.rss.data.db.model

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

data class DbDateTime(
    val millis: Long,
    val zoneOffsetMillis: Int,
) {
    val dateTime: DateTime
        get() = DateTime(millis, DateTimeZone.forOffsetMillis(zoneOffsetMillis))

    companion object {
        fun now(): DbDateTime = DateTime.now().toDb()
    }
}

fun DateTime.toDb(): DbDateTime {
    return DbDateTime(
        millis = millis,
        zoneOffsetMillis = zone.getOffset(millis)
    )
}
