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

    fun onEnableDynamicTheme(value: Boolean)

    fun onChangeTheme(mode: AppTheme)
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

    override fun onEnableDynamicTheme(value: Boolean) {
        firebaseAnalytics.setUserProperty("dynamic_theme_enabled", value.toString())
    }

    override fun onChangeTheme(mode: AppTheme) {
        firebaseAnalytics.setUserProperty(
            "app_theme", when (mode) {
                AppTheme.LIGHT -> "light"
                AppTheme.DARK -> "dark"
                AppTheme.AUTO -> "auto"
            }
        )
    }

    private fun logEvent(event: String, bundle: Bundle? = null) {
        try {
            firebaseAnalytics.logEvent(event, bundle)
        } catch (t: Throwable) {
            Timber.e(t)
        }
    }
}
