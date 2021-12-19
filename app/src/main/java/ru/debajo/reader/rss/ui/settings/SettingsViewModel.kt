package ru.debajo.reader.rss.ui.settings

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.data.preferences.BackgroundUpdatesEnabledPreference
import ru.debajo.reader.rss.ui.arch.BaseViewModel
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.AppThemeProvider

class SettingsViewModel(
    private val appThemeProvider: AppThemeProvider,
    private val backgroundUpdatesEnabledPreference: BackgroundUpdatesEnabledPreference,
) : BaseViewModel() {

    private val stateMutable: MutableStateFlow<SettingsState> = MutableStateFlow(
        SettingsState(
            supportDynamicTheme = appThemeProvider.supportDynamicTheme(),
            backgroundUpdates = backgroundUpdatesEnabledPreference.get(),
        )
    )

    val state: StateFlow<SettingsState> = stateMutable

    init {
        launch {
            appThemeProvider.currentAppThemeConfig.collect { config ->
                stateMutable.value = stateMutable.value.copy(
                    appTheme = config.theme,
                    isDynamicColor = config.dynamic,
                )
            }
        }
    }

    fun toggleDynamicColor() {
        launch {
            appThemeProvider.update(!stateMutable.value.isDynamicColor)
        }
    }

    fun toggleThemeDropDown() {
        val state = stateMutable.value
        stateMutable.value = state.copy(dropDownExpanded = !state.dropDownExpanded)
    }

    fun selectTheme(theme: AppTheme) {
        stateMutable.value = stateMutable.value.copy(dropDownExpanded = false)
        launch {
            appThemeProvider.update(theme)
        }
    }

    fun toggleBackgroundUpdates() {
        val state = stateMutable.value
        stateMutable.value = state.copy(backgroundUpdates = !state.backgroundUpdates)
        launch(IO) {
            backgroundUpdatesEnabledPreference.set(!state.backgroundUpdates)
        }

        // stop scheduler
    }
}
