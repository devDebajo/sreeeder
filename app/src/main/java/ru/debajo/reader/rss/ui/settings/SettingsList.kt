package ru.debajo.reader.rss.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.common.AppCard
import ru.debajo.reader.rss.ui.common.Switch
import ru.debajo.reader.rss.ui.theme.title

@Composable
fun SettingsList(
    viewModel: SettingsViewModel = diViewModel()
) {
    LaunchedEffect(key1 = "SettingsList", block = {
        viewModel.load()
    })

    val state by viewModel.state.collectAsState()

    Column(
        Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsThemeButton(state)
        SettingsDynamicThemeSwitch(state, viewModel)
    }
}

@Composable
private fun ColumnScope.SettingsThemeButton(state: SettingsState) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp, horizontal = 16.dp),
        onClick = {

        }) {
        Row {
            Text(
                stringResource(id = R.string.settings_app_theme),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            OutlinedButton(onClick = { }, contentPadding = PaddingValues(1.dp)) {
                Text(state.appTheme.title, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ColumnScope.SettingsDynamicThemeSwitch(state: SettingsState, viewModel: SettingsViewModel) {
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

//    Popup(popupPositionProvider = object : PopupPositionProvider {
//        override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
//            return IntOffset(0, 0)
//        }
//    }) {
//        Column(Modifier.background(Color.Red).width(150.dp)) {
//            Text("1")
//            Text("2")
//            Text("3")
//            Text("4")
//        }
//    }
