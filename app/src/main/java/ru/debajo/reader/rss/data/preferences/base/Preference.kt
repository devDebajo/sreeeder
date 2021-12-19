package ru.debajo.reader.rss.data.preferences.base

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

abstract class Preference<T : Any?> {

    protected abstract val sharedPreferences: SharedPreferences

    protected abstract val key: String

    protected abstract val defaultValue: () -> T

    protected abstract fun SharedPreferences.getUnsafe(key: String): T

    protected abstract fun SharedPreferences.Editor.setUnsafe(key: String, value: T)

    suspend fun get(): T {
        return runCatching {
            if (sharedPreferences.contains(key)) {
                withContext(IO) { sharedPreferences.getUnsafe(key) }
            } else {
                defaultValue()
            }
        }.getOrElse { defaultValue() }
    }

    suspend fun set(value: T) {
        withContext(IO) {
            if (value == null) {
                sharedPreferences.edit(commit = true) { remove(key) }
            } else {
                runCatching {
                    sharedPreferences.edit(commit = true) { setUnsafe(key, value) }
                }
            }
        }
    }
}
