package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.Switch
import ru.debajo.reader.rss.ui.theme.AppTheme
import ru.debajo.reader.rss.ui.theme.title

@Composable
fun SettingsList(
    viewModel: SettingsViewModel = diViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsThemeButton(state, viewModel)
        SettingsDynamicThemeSwitch(state, viewModel)
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
        AppCard(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 16.dp),
            onClick = {}) {
            Row {
                Text(
                    stringResource(id = R.string.settings_dynamic_theme),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )

                Switch(
                    checked = state.isDynamicColor,
                    onCheckedChange = { viewModel.toggleDynamicColor() }
                )
            }
        }
    }
}
