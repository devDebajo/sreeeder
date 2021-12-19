package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import ru.debajo.reader.rss.di.*
import ru.debajo.reader.rss.metrics.Analytics
import ru.debajo.reader.rss.metrics.TimberProdTree
import ru.debajo.reader.rss.ui.theme.AppThemeProvider
import timber.log.Timber

open class App : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val analytics: Analytics by inject()
    private val themeProvider: AppThemeProvider by inject()

    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber()
        initAnalytics()
        initApp()
    }

    open fun initTimber() {
        Timber.plant(TimberProdTree())
    }

    open fun initAnalytics() = Unit

    private fun initApp() {
        launch {
            analytics.onEnableDynamicTheme(themeProvider.loadCurrentConfig().dynamic)
        }
    }

    private fun initDi() {
        startKoin {
            modules(
                appModule(this@App),
                PreferencesModule,
                NetworkModule,
                DbModule,
                RepositoryModule,
                UseCaseModule,
                ViewModelModule,
                MetricsModule,
            )
        }
    }
}
