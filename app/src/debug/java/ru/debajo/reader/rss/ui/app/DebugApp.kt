package ru.debajo.reader.rss.ui.app

import com.google.firebase.analytics.FirebaseAnalytics
import ru.debajo.reader.rss.di.inject
import timber.log.Timber

class DebugApp : App() {

    private val firebaseAnalytics: FirebaseAnalytics by inject()

    override fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    override fun initAnalytics() {
        firebaseAnalytics.setAnalyticsCollectionEnabled(false)
    }
}
