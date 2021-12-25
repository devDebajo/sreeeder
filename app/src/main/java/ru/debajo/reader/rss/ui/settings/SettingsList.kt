package ru.debajo.reader.rss.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R

@Composable
fun SettingsList(parentNavController: NavController, viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()
    val expandedGroup = rememberSaveable { mutableStateOf(-1) }

    SettingsBackPress(expandedGroup)

    Box {
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsGroup(
                title = stringResource(R.string.settings_group_data),
                state = calculateGroupState(expandedGroup, 0),
                onHeaderClick = { expandedGroup.onGroupHeaderClick(0) }
            ) {
                SettingsBackgroundUpdatesSwitch(state, viewModel)
            }
            SettingsGroup(
                title = stringResource(R.string.settings_group_view),
                state = calculateGroupState(expandedGroup, 1),
                onHeaderClick = { expandedGroup.onGroupHeaderClick(1) }
            ) {
                SettingsThemeButton(state, viewModel)
                SettingsDynamicThemeSwitch(state, viewModel)
            }
            SettingsGroup(
                title = stringResource(R.string.settings_group_app),
                state = calculateGroupState(expandedGroup, 2),
                onHeaderClick = { expandedGroup.onGroupHeaderClick(2) }
            ) {
                SettingsPrivacyPolicy(parentNavController)
                SettingsAppVersion()
                SettingsAnalyticsSwitch(state, viewModel)
            }
        }

        if (state.showAnalyticsAlertDialog) {
            SettingsAnalyticsAlertDialog(viewModel)
        }
    }
}

private fun calculateGroupState(expandedGroup: MutableState<Int>, groupIndex: Int): SettingsGroupState {
    return when (expandedGroup.value) {
        -1 -> SettingsGroupState.IDLE
        groupIndex -> SettingsGroupState.EXPANDED
        else -> SettingsGroupState.HALF_ALPHA
    }
}

private fun MutableState<Int>.onGroupHeaderClick(index: Int) {
    value = if (value == index) -1 else index
}

@Composable
private fun SettingsBackPress(expandedGroup: MutableState<Int>) {
    BackHandler(enabled = expandedGroup.value != -1) {
        expandedGroup.value = -1
    }
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
