package ru.debajo.reader.rss.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart

suspend infix fun <T> Flow<T>.collectTo(target: MutableStateFlow<T>) = collect { target.value = it }

fun <T> Flow<T>.withLeading(item: T): Flow<T> = onStart { emit(item) }
