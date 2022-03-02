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

open class App : Application(),
    CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val backgroundUpdatesScheduler: BackgroundUpdatesScheduler by inject()

    open val diModules: List<Module>
        get() = nonVariantModules(this)

    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber()
        initApp()
    }

    open fun initTimber() {
        //Timber.plant(TimberProdTree())
    }

    private fun initApp() {
        launch {
            backgroundUpdatesScheduler.rescheduleOrCancel()
        }
    }

    private fun initDi() {
        startKoin {
            modules(diModules)
        }
    }
}
