package ru.debajo.reader.rss.ui.app

import android.app.Application
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.context.startKoin
import ru.debajo.reader.rss.di.*
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            modules(
                appModule(this@App),
                NetworkModule,
                DbModule,
                RepositoryModule,
                UseCaseModule,
                ViewModelModule,
            )
        }
    }
}
