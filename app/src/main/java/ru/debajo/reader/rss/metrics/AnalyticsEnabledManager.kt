package ru.debajo.reader.rss.metrics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.reader.rss.data.preferences.MetricsEnabledPreference
import timber.log.Timber

interface AnalyticsEnabledManager {
    suspend fun isEnabled(): Boolean

    suspend fun refresh()

    suspend fun setEnabled(enabled: Boolean)
}

open class AnalyticsEnabledManagerProd(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val metricsEnabledPreference: MetricsEnabledPreference,
) : AnalyticsEnabledManager {

    override suspend fun isEnabled(): Boolean = metricsEnabledPreference.get()

    override suspend fun refresh() {
        setEnabledInternal(isEnabled())
    }

    override suspend fun setEnabled(enabled: Boolean) {
        metricsEnabledPreference.set(enabled)
        setEnabledInternal(enabled)
    }

    protected fun setEnabledInternal(enabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(enabled)
        Timber.tag("AnalyticsEnabledManager").i("Analytics sending state changed: $enabled")
    }
}
