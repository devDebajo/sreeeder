package ru.debajo.reader.rss.ui.main

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

data class ScreenTab(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: String,
) {
    val title: String
        @Composable
        get() = stringResource(id = titleRes)
}
