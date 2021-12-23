package ru.debajo.reader.rss.di

import android.annotation.SuppressLint
import org.koin.dsl.module
import ru.debajo.reader.rss.metrics.AnalyticsEnabledManager
import ru.debajo.reader.rss.metrics.AnalyticsEnabledManagerDebug
import ru.debajo.reader.rss.metrics.AnalyticsEnabledManagerProd

@SuppressLint("MissingPermission")
val MetricsDebugModule = module {
    single { AnalyticsEnabledManagerProd(get(), get(), get(), get()) }
    single<AnalyticsEnabledManager> { AnalyticsEnabledManagerDebug(get(), get(), get(), get()) }
}
