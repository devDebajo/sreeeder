package ru.debajo.reader.rss.ui.app

import org.koin.core.module.Module
import ru.debajo.reader.rss.di.nonVariantModules
import timber.log.Timber

class DebugApp : App() {

    override val diModules: List<Module>
        get() = nonVariantModules(this, this)

    override fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}
