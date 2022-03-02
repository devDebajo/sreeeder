package ru.debajo.reader.rss.ui.theme

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.preferences.AppThemePreference
import ru.debajo.reader.rss.data.preferences.DynamicThemePreference

class AppThemeProvider(
    private val appThemePreference: AppThemePreference,
    private val dynamicThemePreference: DynamicThemePreference,
) {

    private val currentAppThemeConfigMutable: MutableStateFlow<AppThemeConfig> = MutableStateFlow(AppThemeConfig(AppTheme.LIGHT, false))
    val currentAppThemeConfig: StateFlow<AppThemeConfig> = currentAppThemeConfigMutable

    suspend fun loadTheme() {
        currentAppThemeConfigMutable.emit(loadCurrentConfig())
    }

    suspend fun loadCurrentConfig(): AppThemeConfig {
        return withContext(Dispatchers.IO) {
            val currentMode = appThemePreference.get()
            val isDynamicTheme = dynamicThemePreference.get()
            AppThemeConfig(currentMode.orDefault(), isDynamicTheme && supportDynamicTheme())
        }
    }

    suspend fun update(mode: AppTheme) {
        appThemePreference.set(mode)
        updateConfig(mode = mode)
    }

    suspend fun update(dynamicTheme: Boolean) {
        dynamicThemePreference.set(dynamicTheme)
        updateConfig(dynamicTheme = dynamicTheme)
    }

    private suspend fun updateConfig(
        mode: AppTheme = currentAppThemeConfigMutable.value.theme,
        dynamicTheme: Boolean = currentAppThemeConfigMutable.value.dynamic,
    ) {
        currentAppThemeConfigMutable.emit(AppThemeConfig(mode, dynamicTheme))
    }

    fun supportDynamicTheme(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
