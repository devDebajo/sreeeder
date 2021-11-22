package ru.debajo.reader.rss.ui.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.arch.BaseViewModel
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

class SettingsViewModel(
    private val appThemeProvider: AppThemeProvider,
) : BaseViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())

    val state: StateFlow<SettingsState> = stateMutable

    fun load() {
        launch {
            val themeConfig = appThemeProvider.loadCurrentConfig()
            stateMutable.value = stateMutable.value.copy(
                appTheme = themeConfig.theme,
                isDynamicColor = themeConfig.dynamic,
                supportDynamicTheme = appThemeProvider.supportDynamicTheme()
            )
        }
    }

    fun toggleDynamicColor() {
        val state = stateMutable.value
        stateMutable.value = state.copy(isDynamicColor = !state.isDynamicColor)
        launch {
            appThemeProvider.update(!state.isDynamicColor)
        }
    }
}
