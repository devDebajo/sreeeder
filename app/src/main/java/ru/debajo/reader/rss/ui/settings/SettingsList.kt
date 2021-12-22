package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ru.debajo.reader.rss.BuildConfig
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.ext.colorInt
import ru.debajo.reader.rss.ui.main.model.toChromeTabsParams
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.title

@Composable
fun SettingsList(parentNavController: NavController, viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()

    Box {
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsBackgroundUpdatesSwitch(state, viewModel)
            SettingsThemeButton(state, viewModel)
            SettingsDynamicThemeSwitch(state, viewModel)
            SettingsPrivacyPolicy(parentNavController)
            SettingsAnalyticsSwitch(state, viewModel)
            SettingsAppVersion()
        }

        if (state.showAnalyticsAlertDialog) {
            SettingsAnalyticsAlertDialog(viewModel)
        }
    }
}

@Composable
private fun SettingsBackgroundUpdatesSwitch(state: SettingsState, viewModel: SettingsViewModel) {
    SettingsSwitch(
        text = stringResource(id = R.string.settings_background_updates),
        checked = state.backgroundUpdates,
    ) {
        viewModel.toggleBackgroundUpdates()
    }
}

@Composable
private fun SettingsThemeButton(state: SettingsState, viewModel: SettingsViewModel) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        onClick = { viewModel.toggleThemeDropDown() }) {
        Column {
            Row {
                Text(
                    stringResource(id = R.string.settings_app_theme),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
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
}

@Composable
private fun AppThemeWidget(theme: AppTheme, selected: Boolean, modifier: Modifier = Modifier) {
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
private fun SettingsDynamicThemeSwitch(state: SettingsState, viewModel: SettingsViewModel) {
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
private fun SettingsPrivacyPolicy(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.background.colorInt
    SettingsText(stringResource(id = R.string.settings_privacy_policy)) {
        NavGraph.ChromeTabs.navigate(navController, BuildConfig.PRIVACY_POLICY.toChromeTabsParams(backgroundColor))
    }
}

@Composable
private fun SettingsAnalyticsSwitch(
    state: SettingsState,
    viewModel: SettingsViewModel
) {
    SettingsSwitch(
        text = stringResource(R.string.settings_analytics_enabled),
        checked = state.analyticsEnabled
    ) {
        if (state.analyticsEnabled) {
            viewModel.toggleAnalyticsAlert(visible = true)
        } else {
            viewModel.setAnalyticsEnabled(true)
        }
    }
}

@Composable
private fun SettingsAppVersion() {
    val text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME)
    SettingsText(text)
}

@Composable
private fun SettingsAnalyticsAlertDialog(viewModel: SettingsViewModel) {
    AlertDialog(
        title = { Text(stringResource(R.string.settings_analytics_alert_title)) },
        text = { Text(stringResource(R.string.settings_analytics_alert_message)) },
        onDismissRequest = { viewModel.toggleAnalyticsAlert(false) },
        icon = { Icon(Icons.Rounded.Analytics, contentDescription = null) },
        confirmButton = {
            TextButton(onClick = { viewModel.toggleAnalyticsAlert(false) }) {
                Text(stringResource(R.string.settings_analytics_alert_cancel))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.setAnalyticsEnabled(false)
                viewModel.toggleAnalyticsAlert(false)
            }) {
                Text(stringResource(R.string.settings_analytics_alert_ok))
            }
        }
    )
}

@Composable
private fun SettingsText(text: String, onClick: (() -> Unit)? = null) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SettingsSwitch(text: String, checked: Boolean, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
                )
            )
        }
    }
}
