package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import ru.debajo.reader.rss.data.preferences.base.BooleanPreference

class CrashlyticsPreference(
    override val sharedPreferences: SharedPreferences,
) : BooleanPreference() {

    override val defaultValue: () -> Boolean = { true }

    override val key: String = "send_crashes"
}
