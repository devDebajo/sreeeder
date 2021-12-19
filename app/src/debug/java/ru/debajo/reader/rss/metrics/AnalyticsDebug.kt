package ru.debajo.reader.rss.metrics

import ru.debajo.reader.rss.ui.theme.AppTheme
import timber.log.Timber

class AnalyticsDebug : Analytics {

    override fun onLoadChannel() {
        log("onLoadChannel")
    }

    override fun onShareChannel() {
        log("onShareChannel")
    }

    override fun onSubscribeChannel() {
        log("onSubscribeChannel")
    }

    override fun onUnsubscribeChannel() {
        log("onUnsubscribeChannel")
    }

    override fun onBookmark() {
        log("onBookmark")
    }

    override fun onRemoveBookmark() {
        log("onRemoveBookmark")
    }

    override fun onEnableDynamicTheme(value: Boolean) {
        log("onEnableDynamicTheme")
    }

    override fun onChangeTheme(mode: AppTheme) {
        log("onChangeTheme")
    }

    private fun log(message: String) {
        Timber.tag("AnalyticsDebug").d(message)
    }
}
