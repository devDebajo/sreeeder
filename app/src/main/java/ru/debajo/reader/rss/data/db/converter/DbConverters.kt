package ru.debajo.reader.rss.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.data.db.model.DbDateTime
import ru.debajo.reader.rss.di.inject

class DbDateTimeConverter {

    private val gson: Gson by inject()

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

    @TypeConverter
    fun to(stringList: List<String>): String = stringList.toJson()

    @TypeConverter
    fun from(json: String): List<String> = json.fromJson()

    private inline fun <reified T> List<T>.toJson(): String {
        return gson.toJson(this)
    }

    private inline fun <reified T> String.fromJson(): List<T> {
        return gson.fromJson(this, object : TypeToken<List<T>>() {}.type)
    }
}
