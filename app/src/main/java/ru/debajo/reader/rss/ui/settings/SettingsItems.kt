package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.ext.optionalClickable
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.title

@Composable
fun SettingsBackgroundUpdatesSwitch(state: SettingsState, viewModel: SettingsViewModel) {
    SettingsSwitch(
        text = stringResource(id = R.string.settings_background_updates),
        checked = state.backgroundUpdates,
    ) {
        viewModel.toggleBackgroundUpdates()
    }
}

@Composable
fun SettingsThemeButton(state: SettingsState, viewModel: SettingsViewModel) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { viewModel.toggleThemeDropDown() }
            .padding(vertical = 18.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                stringResource(id = R.string.settings_app_theme),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            AppThemeWidget(state.appTheme, true)
        }

        AnimatedVisibility(visible = state.dropDownExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                for (theme in state.dropDownThemes) {
                    AppThemeWidget(
                        theme = theme,
                        selected = false,
                        modifier = Modifier.clickable { viewModel.selectTheme(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppThemeWidget(theme: AppTheme, selected: Boolean, modifier: Modifier = Modifier) {
    val color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondary
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(8.dp)
            .then(modifier)

    ) {
        Text(theme.title, fontSize = 10.sp, color = textColor)
    }
}

@Composable
fun SettingsDynamicThemeSwitch(state: SettingsState, viewModel: SettingsViewModel) {
    if (state.supportDynamicTheme) {
        SettingsSwitch(
            text = stringResource(id = R.string.settings_dynamic_theme),
            checked = state.isDynamicColor,
        ) {
            viewModel.toggleDynamicColor()
        }
    }
}

@Composable
fun SettingsPrivacyPolicy(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background.colorInt
    SettingsText(stringResource(id = R.string.settings_privacy_policy)) {
        NavGraph.ChromeTabs.navigate(navController, BuildConfig.PRIVACY_POLICY.toChromeTabsParams(backgroundColor))
    }
}

@Composable
fun SettingsAnalyticsSwitch(
    state: SettingsState,
    viewModel: SettingsViewModel
) {
    SettingsSwitch(
        text = stringResource(R.string.settings_analytics_enabled),
        checked = state.analyticsEnabled,
    ) {
        if (state.analyticsEnabled) {
            viewModel.toggleAnalyticsAlert(visible = true)
        } else {
            viewModel.setAnalyticsEnabled(true)
        }
    }
}

@Composable
fun SettingsAppVersion() {
    SettingsText(stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME))
}

@Composable
fun SettingsExportOpml(viewModel: SettingsViewModel) {
    SettingsText(stringResource(R.string.settings_export_opml)) {
        viewModel.onExportOpmlClick()
    }
}

@Composable
fun SettingsImportOpml(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable { viewModel.onImportOpmlClick() }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 18.dp, horizontal = 16.dp),
            text = stringResource(R.string.settings_import_opml),
            fontSize = 14.sp
        )
        if (state.importing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
