package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.debajo.reader.rss.data.preferences.BackgroundUpdatesEnabledPreference
import ru.debajo.reader.rss.data.preferences.UseEmbeddedWebPageRenderPreference
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesScheduler
import ru.debajo.reader.rss.di.MetricsProdModule
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.di.nonVariantModules
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.metrics.AnalyticsEnabledManager
import ru.debajo.reader.rss.metrics.TimberProdTree
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import timber.log.Timber

open class App : Application(),
    CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val analytics: Analytics by inject()
    private val themeProvider: AppThemeProvider by inject()
    private val backgroundUpdatesEnabledPreference: BackgroundUpdatesEnabledPreference by inject()
    private val useEmbeddedWebPageRenderPreference: UseEmbeddedWebPageRenderPreference by inject()
    private val backgroundUpdatesScheduler: BackgroundUpdatesScheduler by inject()
    private val analyticsEnabledManager: AnalyticsEnabledManager by inject()

    open val diModules: List<Module>
        get() = nonVariantModules(this) + listOf(MetricsProdModule)

    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber()
        initApp()
    }

    open fun initTimber() {
        Timber.plant(TimberProdTree())
    }

    private fun initApp() {
        launch {
            analyticsEnabledManager.refresh()
            updateProperties()
            backgroundUpdatesScheduler.rescheduleOrCancel()
        }
    }

    private suspend fun updateProperties() {
        val themeConfig = themeProvider.loadCurrentConfig()
        analytics.setDynamicThemeUserProperty(themeConfig.dynamic)
        analytics.setThemeUserProperty(themeConfig.theme)

        analytics.setAnalyticsEnabled(analyticsEnabledManager.isEnabled())
        analytics.setBackgroundUpdatesToggleState(backgroundUpdatesEnabledPreference.get())
        analytics.setUseEmbeddedWebRender(useEmbeddedWebPageRenderPreference.get())
    }

    private fun initDi() {
        startKoin {
            modules(diModules)
        }
    }
}
