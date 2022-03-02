package ru.debajo.reader.rss.domain.error

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class TimberProdTree(
    private val coroutineScope: CoroutineScope,
    private val sendErrorsUseCase: SendErrorsUseCase,
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null && priority == Log.ERROR) {
            coroutineScope.launch {
                sendErrorsUseCase.record(t, false, message, tag)
            }
        }
    }
}
