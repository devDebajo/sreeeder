package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import ru.debajo.reader.rss.data.preferences.base.BooleanPreference

class ShowNavigationTitlesPreference(
    override val sharedPreferences: SharedPreferences,
) : BooleanPreference() {

    override val defaultValue: () -> Boolean = { true }

    override val key: String = "show_navigation_titles"
}
