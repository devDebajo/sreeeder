package ru.debajo.reader.rss.ui.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarScrollState
import androidx.compose.material3.rememberTopAppBarScrollState
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
@OptIn(ExperimentalMaterial3Api::class)
fun rememberEnterAlwaysScrollBehavior(
    topAppBarScrollState: TopAppBarScrollState = rememberTopAppBarScrollState()
): TopAppBarScrollBehavior {
    return remember(topAppBarScrollState) {
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarScrollState)
    }
}
