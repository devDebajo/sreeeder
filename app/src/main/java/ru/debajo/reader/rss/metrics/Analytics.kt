package ru.debajo.reader.rss.metrics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class Analytics(
    private val firebaseAnalytics: FirebaseAnalytics,
) {
    fun onLoadChannel() {
        logEvent("load_channel")
    }

    fun onShareChannel() {
        logEvent("share_channel")
    }

    fun onSubscribeChannel() {
        logEvent("subscribe_channel")
    }

    fun onUnsubscribeChannel() {
        logEvent("unsubscribe_channel")
    }

    fun onBookmark() {
        logEvent("bookmarked_add")
    }

    fun onRemoveBookmark() {
        logEvent("bookmark_remove")
    }

    fun onEnableDynamicTheme(value: Boolean) {
        firebaseAnalytics.setUserProperty("dynamic_theme_enabled", value.toString())
    }

    private fun logEvent(event: String, bundle: Bundle? = null) {
        try {
            firebaseAnalytics.logEvent(event, bundle)
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }
}
