package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import ru.debajo.reader.rss.data.error.SendErrorsScheduler
import ru.debajo.reader.rss.data.updater.BackgroundUpdatesScheduler
import ru.debajo.reader.rss.di.inject
import ru.debajo.reader.rss.di.nonVariantModules
import ru.debajo.reader.rss.domain.error.SendErrorsUseCase
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

open class App : Application(), CoroutineScope by CoroutineScope(SupervisorJob()) {

    private val sendErrorsUseCase: SendErrorsUseCase by inject()
    private val backgroundUpdatesScheduler: BackgroundUpdatesScheduler by inject()
    private val sendErrorsScheduler: SendErrorsScheduler by inject()
    private val themeProvider: AppThemeProvider by inject()

    open val diModules: List<Module>
        get() = nonVariantModules(this)

    final override fun onCreate() {
        super.onCreate()

        initDi()
        initTimber(sendErrorsUseCase)
        //initErrors()
        initApp()
    }

    open fun initTimber(sendErrorsUseCase: SendErrorsUseCase) {
        //Timber.plant(TimberProdTree(this, sendErrorsUseCase))
    }

    private fun initErrors() {
        launch { sendErrorsUseCase.sendAllPending() }
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            runBlocking { sendErrorsUseCase.record(e, true) }
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun initApp() {
        launch {
            themeProvider.loadTheme()
            //backgroundUpdatesScheduler.rescheduleOrCancel()
            sendErrorsScheduler.rescheduleOrCancel()
        }
    }

    private fun initDi() {
        startKoin {
            modules(diModules)
        }
    }
}
