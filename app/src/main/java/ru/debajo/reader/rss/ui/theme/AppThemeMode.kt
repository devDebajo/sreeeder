package ru.debajo.reader.rss.ui.theme

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import ru.debajo.reader.rss.R

@Stable
enum class AppTheme(@StringRes val titleRes: Int, private val minApiVersion: Int) {
    LIGHT(R.string.theme_light, Build.VERSION_CODES.BASE),
    DARK(R.string.theme_dark, Build.VERSION_CODES.BASE),
    AUTO(R.string.theme_auto, Build.VERSION_CODES.Q);

    val isValid: Boolean
        get() = Build.VERSION.SDK_INT >= minApiVersion

    fun orDefault(): AppTheme = if (isValid) this else LIGHT
}

val AppTheme.title: String
    @Composable
    get() = stringResource(id = titleRes)
