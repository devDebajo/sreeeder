package ru.debajo.reader.rss.data.db.converter

import androidx.room.TypeConverter
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.data.db.model.DbDateTime

class DbDateTimeConverter {
    @TypeConverter
    fun to(value: DbDateTime?): Long? {
        return value?.dateTime?.withZone(DateTimeZone.UTC)?.millis
    }

    @TypeConverter
    fun from(value: Long?): DbDateTime? {
        value ?: return null
        return DbDateTime(
            millis = value,
            zoneOffsetMillis = 0 // UTC
        )
    }
}
