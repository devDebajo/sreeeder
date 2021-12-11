package ru.debajo.reader.rss.data.preferences

import android.content.SharedPreferences
import ru.debajo.reader.rss.data.preferences.base.Preference
import ru.debajo.reader.rss.ui.theme.AppTheme

class AppThemePreference(
    override val sharedPreferences: SharedPreferences,
) : Preference<AppTheme>() {

    override val key: String = "current_theme"

    override val defaultValue: () -> AppTheme = { AppTheme.LIGHT }

    override fun SharedPreferences.getUnsafe(key: String): AppTheme {
        return AppTheme.values()[getInt(key, 0)]
    }

    override fun SharedPreferences.Editor.setUnsafe(key: String, value: AppTheme) {
        putInt(key, value.ordinal)
    }
}