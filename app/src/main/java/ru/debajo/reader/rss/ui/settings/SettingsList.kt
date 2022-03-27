package ru.debajo.reader.rss.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.common.rememberSaveableMutableState
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.settingsTab

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsList(
    innerPadding: PaddingValues,
    parentNavController: NavController,
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()
    val expandedGroup = rememberSaveableMutableState(-1)

    SettingsBackPress(expandedGroup)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MainTopBar(settingsTab) }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    SettingsExportOpml(viewModel)
                    SettingsImportOpml(viewModel)
                }
                SettingsGroup(
                    title = stringResource(R.string.settings_group_view),
                    state = calculateGroupState(expandedGroup, 1),
                    onHeaderClick = { expandedGroup.onGroupHeaderClick(1) }
                ) {
                    SettingsThemeButton(state, viewModel)
                    SettingsDynamicThemeSwitch(state, viewModel)
                    SettingsUseWebRenderSwitch(state, viewModel)
                    SettingsShowNavigationTitlesSwitch(state, viewModel)
                }
                SettingsGroup(
                    title = stringResource(R.string.settings_group_app),
                    state = calculateGroupState(expandedGroup, 2),
                    onHeaderClick = { expandedGroup.onGroupHeaderClick(2) }
                ) {
                    SettingsPrivacyPolicy(parentNavController)
                    SettingsAppVersion()
                }
            }

            val snackBar by viewModel.snackBar.collectAsState()
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = snackBar.visible,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
            ) {
                val haptic = LocalHapticFeedback.current
                LaunchedEffect(key1 = snackBar.visible, block = {
                    if (snackBar.visible) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                })
                Box(
                    Modifier
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = snackBar.message,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
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
