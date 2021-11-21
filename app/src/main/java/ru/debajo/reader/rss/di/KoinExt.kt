package ru.debajo.reader.rss.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.java.KoinJavaComponent

@Composable
inline fun <reified VM : ViewModel> diViewModel(key: String? = null): VM {
    return viewModel(
        key = key,
        factory = KoinViewModelFactory
    )
}

inline fun <reified T : Any> getFromDi(): T {
    return KoinJavaComponent.get(T::class.java)
}

inline fun <reified T : Any> inject(): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java)
}