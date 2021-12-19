package ru.debajo.reader.rss.di

import android.annotation.SuppressLint
import org.koin.dsl.module
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.metrics.AnalyticsDebug

@SuppressLint("MissingPermission")
val MetricsDebugModule = module {
    single<Analytics> { AnalyticsDebug() }
}
