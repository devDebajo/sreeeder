package ru.debajo.reader.rss.di

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

inline fun <reified VM : ViewModel> ComponentActivity.diViewModels(): Lazy<VM> {
    return viewModels(factoryProducer = { KoinViewModelFactory })
}

inline fun <reified T : Any> getFromDi(): T {
    return KoinJavaComponent.get(T::class.java)
}

@Composable
inline fun <reified T : Any> rememberFromDi(): T {
    return remember { getFromDi() }
}

inline fun <reified T : Any> inject(): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java)
}