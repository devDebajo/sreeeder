package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesScheduler
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.di.nonVariantModules
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

open class App : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val backgroundUpdatesScheduler: BackgroundUpdatesScheduler by inject()
    private val themeProvider: AppThemeProvider by inject()

    open val diModules: List<Module>
        get() = nonVariantModules(this)

    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber()
        initApp()
    }

    open fun initTimber() = Unit

    private fun initApp() {
        launch {
            themeProvider.loadTheme()
            backgroundUpdatesScheduler.rescheduleOrCancel()
        }
    }

    private fun initDi() {
        startKoin {
            modules(diModules)
        }
    }
}
