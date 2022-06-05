package ru.debajo.reader.rss.ui.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun <T> rememberMutableState(initialState: T): MutableState<T> {
    return remember { mutableStateOf(initialState) }
}

@Composable
fun <T> rememberSaveableMutableState(initialState: T): MutableState<T> {
    return rememberSaveable { mutableStateOf(initialState) }
}

@Composable
fun <T> rememberMutableState(
    vararg keys: Any?,
    initialState: T
): MutableState<T> {
    return remember(*keys) { mutableStateOf(initialState) }
}

@Composable
fun <T> rememberSaveableMutableState(vararg keys: Any?, initialState: T): MutableState<T> {
    return rememberSaveable(*keys) { mutableStateOf(initialState) }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun rememberEnterAlwaysScrollBehavior(
    topAppBarScrollState: TopAppBarScrollState = rememberTopAppBarScrollState()
): TopAppBarScrollBehavior {
    return remember(topAppBarScrollState) {
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarScrollState)
    }
}
