package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.debajo.reader.rss.ui.common.AppCard

enum class SettingsGroupState {
    IDLE,
    EXPANDED,
    HALF_ALPHA
}

@Composable
fun SettingsGroup(
    title: String,
    state: SettingsGroupState,
    onHeaderClick: () -> Unit,
    block: @Composable () -> Unit
) {
    AppCard(Modifier.fillMaxWidth()) {
        Column {
            SettingsText(
                alpha = if (state == SettingsGroupState.HALF_ALPHA) 0.2f else 1f,
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                onClick = onHeaderClick,
            )
            AnimatedVisibility(visible = state == SettingsGroupState.EXPANDED) {
                Column {
                    block()
                }
            }
        }
    }
}
