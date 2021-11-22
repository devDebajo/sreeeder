package ru.debajo.reader.rss.ui.settings

import androidx.compose.runtime.Stable
import ru.debajo.reader.rss.ui.theme.AppTheme

@Stable
data class SettingsState(
    val appTheme: AppTheme = AppTheme.LIGHT,
    val isDynamicColor: Boolean = false,
    val supportDynamicTheme: Boolean = false,
)
