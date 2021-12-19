package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import ru.debajo.reader.rss.data.preferences.base.BooleanPreference

class BackgroundUpdatesEnabledPreference(
    override val sharedPreferences: SharedPreferences,
) : BooleanPreference() {

    override val key: String = "enable_background_updates"
}
