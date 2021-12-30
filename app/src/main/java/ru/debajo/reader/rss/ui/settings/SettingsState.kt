package ru.debajo.reader.rss.ui.settings

import androidx.compose.runtime.Stable
import ru.debajo.reader.rss.ui.theme.AppTheme

@Stable
data class SettingsState(
    val appTheme: AppTheme = AppTheme.LIGHT,
    val dropDownExpanded: Boolean = false,
    val isDynamicColor: Boolean = false,
    val supportDynamicTheme: Boolean = false,
    val backgroundUpdates: Boolean = true,
    val analyticsEnabled: Boolean = true,
    val showAnalyticsAlertDialog: Boolean = false,
    val importing: Boolean = false,
) {

    val dropDownThemes: List<AppTheme>
        get() = AppTheme.values().filter { it.isValid && it != appTheme }
}
