package ru.debajo.reader.rss.metrics

import android.os.Bundle
import androidx.annotation.Size
import com.google.firebase.analytics.FirebaseAnalytics
import ru.debajo.reader.rss.ui.theme.AppTheme
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

    fun setDynamicThemeUserProperty(value: Boolean) {
        setUserProperty("dynamic_theme_enabled", value)
    }

    fun setThemeUserProperty(mode: AppTheme) {
        setUserProperty(
            "app_theme", when (mode) {
                AppTheme.LIGHT -> "light"
                AppTheme.DARK -> "dark"
                AppTheme.AUTO -> "auto"
            }
        )
    }

    fun setBackgroundUpdatesToggleState(enabled: Boolean) {
        setUserProperty("bg_updates_toggle_state", enabled)
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        setUserProperty("analytics_enabled", enabled)
    }

    fun onStartWorker() {
        logEvent("worker_start")
    }

    fun onSuccessWorker() {
        logEvent("worker_success")
    }

    fun onFailWorker() {
        logEvent("worker_fail")
    }

    private fun setUserProperty(@Size(min = 1L, max = 24L) name: String, value: Any) {
        val valueStr = value.toString().take(36)
        Timber.tag("Analytics").d("Set user property $name with value $valueStr")
        firebaseAnalytics.setUserProperty(name, valueStr)
    }

    private fun logEvent(event: String, bundle: Bundle? = null) {
        try {
            Timber.tag("Analytics").d("On event [$event], parameters: $bundle")
            firebaseAnalytics.logEvent(event, bundle)
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }
}
