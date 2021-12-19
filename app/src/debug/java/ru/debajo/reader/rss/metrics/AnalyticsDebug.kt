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

    override fun setDynamicThemeUserProperty(value: Boolean) {
        log("onEnableDynamicTheme $value")
    }

    override fun setThemeUserProperty(mode: AppTheme) {
        log("onChangeTheme $mode")
    }

    override fun setBackgroundUpdatesToggleState(enabled: Boolean) {
        log("setBackgroundUpdatesToggleState $enabled")
    }

    override fun onStartWorker() {
        log("onStartWorker")
    }

    override fun onSuccessWorker() {
        log("onSuccessWorker")
    }

    override fun onFailWorker() {
        log("onFailWorker")
    }

    private fun log(message: String) {
        Timber.tag("AnalyticsDebug").d(message)
    }
}
