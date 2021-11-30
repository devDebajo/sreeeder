package ru.debajo.reader.rss.ui.main.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ru.debajo.reader.rss.ui.main.navigation.UnitNavigationNode

data class ScreenTab(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val navigation: UnitNavigationNode,
) {
    val title: String
        @Composable
        get() = stringResource(id = titleRes)
}
