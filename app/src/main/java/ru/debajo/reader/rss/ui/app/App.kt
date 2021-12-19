package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.debajo.reader.rss.di.MetricsProdModule
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.di.nonVariantModules
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.metrics.TimberProdTree
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import timber.log.Timber

open class App : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val analytics: Analytics by inject()
    private val themeProvider: AppThemeProvider by inject()

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
            val themeConfig = themeProvider.loadCurrentConfig()
            analytics.onEnableDynamicTheme(themeConfig.dynamic)
            analytics.onChangeTheme(themeConfig.theme)
        }
    }

    private fun initDi() {
        startKoin {
            modules(diModules)
        }
    }
}
