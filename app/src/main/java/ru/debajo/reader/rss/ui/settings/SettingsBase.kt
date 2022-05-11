package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.ui.ext.optionalClickable

@Composable
fun SettingsText(
    text: String,
    alpha: Float = 1f,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight? = null,
    onClick: (() -> Unit)? = null,
) {
    val alphaAnimatable = remember(text) { Animatable(1f) }
    LaunchedEffect(key1 = text, key2 = alpha, block = {
        if (alphaAnimatable.value != alpha) {
            alphaAnimatable.animateTo(alpha)
        }
    })
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .optionalClickable(onClick)
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .alpha(alphaAnimatable.value),
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

@Composable
fun SettingsSwitch(text: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        )

        val haptic = LocalHapticFeedback.current
        Switch(
            checked = checked,
            onCheckedChange = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        )
    }
}

private fun <T, V : AnimationVector> Animatable<T, V>.launchAnimateTo(
    scope: CoroutineScope,
    targetValue: T,
    durationMs: Int = 500
) {
    scope.launch { animateTo(targetValue, tween(durationMs)) }
}