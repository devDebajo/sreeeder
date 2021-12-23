package ru.debajo.reader.rss.metrics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.reader.rss.data.preferences.MetricsEnabledPreference

class AnalyticsEnabledManagerDebug(
    analytics: Analytics,
    firebaseAnalytics: FirebaseAnalytics,
    firebaseCrashlytics: FirebaseCrashlytics,
    private val metricsEnabledPreference: MetricsEnabledPreference,
) : AnalyticsEnabledManagerProd(analytics, firebaseAnalytics, firebaseCrashlytics, metricsEnabledPreference) {

    override suspend fun refresh() {
        setEnabledInternal(false)
    }

    override suspend fun setEnabled(enabled: Boolean) {
        metricsEnabledPreference.set(enabled)
        setEnabledInternal(false)
    }
}