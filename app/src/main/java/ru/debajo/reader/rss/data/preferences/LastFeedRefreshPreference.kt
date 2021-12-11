package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import ru.debajo.reader.rss.data.preferences.base.Preference

class LastFeedRefreshPreference(
    override val sharedPreferences: SharedPreferences,
) : Preference<DateTime?>() {

    override val key: String = "last_feed_refresh"

    override val defaultValue: () -> DateTime? = { null }

    override fun SharedPreferences.getUnsafe(key: String): DateTime {
        return DateTime(getLong(key, 0L), DateTimeZone.UTC)
    }

    override fun SharedPreferences.Editor.setUnsafe(key: String, value: DateTime?) {
        putLong(key, value!!.withZone(DateTimeZone.UTC).millis)
    }
}
