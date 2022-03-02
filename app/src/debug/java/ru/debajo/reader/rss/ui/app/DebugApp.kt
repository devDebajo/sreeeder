package ru.debajo.reader.rss.ui.app

import org.koin.core.module.Module
import ru.debajo.reader.rss.di.nonVariantModules
import ru.debajo.reader.rss.domain.error.SendErrorsUseCase
import ru.debajo.reader.rss.domain.error.TimberDebugTree
import timber.log.Timber

class DebugApp : App() {

    override val diModules: List<Module>
        get() = nonVariantModules(this)

    override fun initTimber(sendErrorsUseCase: SendErrorsUseCase) {
        Timber.plant(TimberDebugTree(this, sendErrorsUseCase))
    }
}
