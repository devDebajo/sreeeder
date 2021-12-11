package ru.debajo.reader.rss.data.preferences.base

import android.content.SharedPreferences

abstract class BooleanPreference : Preference<Boolean>() {

    override val defaultValue: () -> Boolean = { false }

    override fun SharedPreferences.getUnsafe(key: String): Boolean = getBoolean(key, defaultValue())

    override fun SharedPreferences.Editor.setUnsafe(key: String, value: Boolean) {
        putBoolean(key, value)
    }
}
