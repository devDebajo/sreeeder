package ru.debajo.reader.rss.ui.app

import org.koin.core.module.Module
import ru.debajo.reader.rss.di.MetricsDebugModule
import ru.debajo.reader.rss.di.nonVariantModules
import timber.log.Timber

class DebugApp : App() {

    override val diModules: List<Module>
        get() = nonVariantModules(this) + listOf(MetricsDebugModule)

    override fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    override suspend fun initAnalytics() {
        analyticsEnabledManager.setEnabled(false)
    }
}
