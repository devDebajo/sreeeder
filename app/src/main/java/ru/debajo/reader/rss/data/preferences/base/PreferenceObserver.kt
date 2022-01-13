package ru.debajo.reader.rss.data.preferences.base

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PreferenceObserver {
    fun <T> observe(preference: Preference<T>): Flow<T> {
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (preference.key == key) {
                    trySendBlocking(preference.getBlocking())
                }
            }
            preference.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { preference.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }
    }
}
