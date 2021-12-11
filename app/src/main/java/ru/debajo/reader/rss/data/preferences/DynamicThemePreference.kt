package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import ru.debajo.reader.rss.data.preferences.base.BooleanPreference

class DynamicThemePreference(
    override val sharedPreferences: SharedPreferences,
) : BooleanPreference() {

    override val key: String = "is_dynamic_theme"
}
