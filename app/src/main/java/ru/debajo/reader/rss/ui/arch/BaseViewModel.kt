package ru.debajo.reader.rss.ui.arch

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseViewModel : ViewModel(), CoroutineScope by CoroutineScope(SupervisorJob() + Main) {

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}
