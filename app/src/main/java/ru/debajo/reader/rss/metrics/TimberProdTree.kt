package ru.debajo.reader.rss.metrics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import ru.debajo.reader.rss.di.inject
import timber.log.Timber

class TimberProdTree : Timber.Tree() {

    private val firebaseCrashlytics: FirebaseCrashlytics by inject()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            firebaseCrashlytics.recordException(t)
        }
    }
}
