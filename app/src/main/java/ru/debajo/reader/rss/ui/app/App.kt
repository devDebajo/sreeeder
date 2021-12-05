package ru.debajo.reader.rss.ui.app

import android.app.Application
import org.koin.core.context.startKoin
import ru.debajo.reader.rss.di.*
import ru.debajo.reader.rss.metrics.TimberProdTree
import timber.log.Timber

open class App : Application() {
    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber()
        initAnalytics()
    }

    open fun initTimber() {
        Timber.plant(TimberProdTree())
    }

    open fun initAnalytics() = Unit

    private fun initDi() {
        startKoin {
            modules(
                appModule(this@App),
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
