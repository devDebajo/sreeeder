package ru.debajo.reader.rss.metrics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import ru.debajo.reader.rss.ui.theme.AppTheme
import timber.log.Timber

interface Analytics {
    fun onLoadChannel()

    fun onShareChannel()

    fun onSubscribeChannel()

    fun onUnsubscribeChannel()

    fun onBookmark()

    fun onRemoveBookmark()

    fun setDynamicThemeUserProperty(value: Boolean)

    fun setThemeUserProperty(mode: AppTheme)

    fun setBackgroundUpdatesToggleState(enabled: Boolean)

    fun onStartWorker()

    fun onSuccessWorker()

    fun onFailWorker()
}

class AnalyticsProd(
    private val firebaseAnalytics: FirebaseAnalytics,
) : Analytics {

    override fun onLoadChannel() {
        logEvent("load_channel")
    }

    override fun onShareChannel() {
        logEvent("share_channel")
    }

    override fun onSubscribeChannel() {
        logEvent("subscribe_channel")
    }

    override fun onUnsubscribeChannel() {
        logEvent("unsubscribe_channel")
    }

    override fun onBookmark() {
        logEvent("bookmarked_add")
    }

    override fun onRemoveBookmark() {
        logEvent("bookmark_remove")
    }

    override fun setDynamicThemeUserProperty(value: Boolean) {
        firebaseAnalytics.setUserProperty("dynamic_theme_enabled", value.toString())
    }

    override fun setThemeUserProperty(mode: AppTheme) {
        firebaseAnalytics.setUserProperty(
            "app_theme", when (mode) {
                AppTheme.LIGHT -> "light"
                AppTheme.DARK -> "dark"
                AppTheme.AUTO -> "auto"
            }
        )
    }

    override fun setBackgroundUpdatesToggleState(enabled: Boolean) {
        firebaseAnalytics.setUserProperty("bg_updates_toggle_state", enabled.toString())
    }

    override fun onStartWorker() {
        logEvent("worker_start")
    }

    override fun onSuccessWorker() {
        logEvent("worker_success")
    }

    override fun onFailWorker() {
        logEvent("worker_fail")
    }

    private fun logEvent(event: String, bundle: Bundle? = null) {
        try {
            firebaseAnalytics.logEvent(event, bundle)
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }
}
