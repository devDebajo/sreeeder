package ru.debajo.reader.rss.metrics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.reader.rss.data.preferences.MetricsEnabledPreference
import timber.log.Timber

class AnalyticsEnabledManager(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val metricsEnabledPreference: MetricsEnabledPreference,
) {
    suspend fun isEnabled(): Boolean = metricsEnabledPreference.get()

    suspend fun refresh() {
        setEnabledInternal(isEnabled())
    }

    suspend fun setEnabled(enabled: Boolean) {
        metricsEnabledPreference.set(enabled)
        setEnabledInternal(enabled)
    }

    private fun setEnabledInternal(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(enabled)
        Timber.tag("AnalyticsEnabledManager").i("Analytics sending state changed: $enabled")
    }
}
