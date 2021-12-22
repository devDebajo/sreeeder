package ru.debajo.reader.rss.metrics

import ru.debajo.reader.rss.ui.theme.AppTheme

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

