package ru.debajo.reader.rss.data.preferences.base

import android.content.SharedPreferences
import androidx.core.content.edit

abstract class Preference<T : Any?> {

    protected abstract val sharedPreferences: SharedPreferences

    protected abstract val key: String

    protected abstract val defaultValue: () -> T

    protected abstract fun SharedPreferences.getUnsafe(key: String): T

    protected abstract fun SharedPreferences.Editor.setUnsafe(key: String, value: T)

    fun get(): T {
        return runCatching {
            if (sharedPreferences.contains(key)) {
                sharedPreferences.getUnsafe(key)
            } else {
                defaultValue()
            }
        }.getOrElse { defaultValue() }
    }

    fun set(value: T) {
        if (value == null) {
            sharedPreferences.edit { remove(key) }
        } else {
            runCatching {
                sharedPreferences.edit { setUnsafe(key, value) }
            }
        }
    }
}
