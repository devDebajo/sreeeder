package ru.debajo.reader.rss.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

suspend fun <T> Flow<T>.collectTo(target: MutableStateFlow<T>) {
    collect { target.value = it }
}
