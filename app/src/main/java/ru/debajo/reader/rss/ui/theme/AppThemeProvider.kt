package ru.debajo.reader.rss.ui.theme

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.metrics.Analytics

class AppThemeProvider(
    private val sharedPreferences: SharedPreferences,
    private val analytics: Analytics,
) {

    private val currentAppThemeConfigMutable: MutableStateFlow<AppThemeConfig> = MutableStateFlow(AppThemeConfig(AppTheme.LIGHT, false))
    val currentAppThemeConfig: StateFlow<AppThemeConfig> = currentAppThemeConfigMutable

    suspend fun loadTheme() {
        currentAppThemeConfigMutable.emit(loadCurrentConfig())
    }

    suspend fun loadCurrentConfig(): AppThemeConfig {
        return withContext(Dispatchers.IO) {
            val currentMode = runCatching {
                AppTheme.values()[sharedPreferences.getInt(THEME_KEY, AppTheme.DARK.ordinal)]
            }.getOrElse { AppTheme.LIGHT }
            val isDynamicTheme = sharedPreferences.getBoolean(DYNAMIC_THEME_KEY, false)

            AppThemeConfig(
                currentMode.orDefault(),
                isDynamicTheme && supportDynamicTheme()
            )
        }
    }

    suspend fun update(mode: AppTheme) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit(commit = true) {
                putInt(THEME_KEY, mode.ordinal)
            }
        }
        updateConfig(mode = mode)
    }

    suspend fun update(dynamicTheme: Boolean) {
        analytics.onEnableDynamicTheme(dynamicTheme)
        withContext(Dispatchers.IO) {
            sharedPreferences.edit(commit = true) {
                putBoolean(DYNAMIC_THEME_KEY, dynamicTheme)
            }
        }
        updateConfig(dynamicTheme = dynamicTheme)
    }

    private suspend fun updateConfig(
        mode: AppTheme = currentAppThemeConfigMutable.value.theme,
        dynamicTheme: Boolean = currentAppThemeConfigMutable.value.dynamic,
    ) {
        currentAppThemeConfigMutable.emit(AppThemeConfig(mode, dynamicTheme))
    }

    fun supportDynamicTheme(): Boolean = DynamicColors.isDynamicColorAvailable()

    private companion object {
        const val THEME_KEY = "current_theme"
        const val DYNAMIC_THEME_KEY = "is_dynamic_theme"
    }
}
