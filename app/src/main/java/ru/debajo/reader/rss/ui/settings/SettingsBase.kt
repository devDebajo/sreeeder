package ru.debajo.reader.rss.ui.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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

@Composable
private fun Switch(
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    val checkedBgColor = MaterialTheme.colorScheme.primaryContainer
    val uncheckedBgColor = MaterialTheme.colorScheme.secondary
    val checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer
    val uncheckedThumbColor = MaterialTheme.colorScheme.onSecondary

    val bgAnimator = remember { androidx.compose.animation.Animatable(uncheckedBgColor) }
    val thumbAnimator = remember { androidx.compose.animation.Animatable(uncheckedThumbColor) }
    val thumbOffsetAnimator = remember { Animatable(0f) }
    LaunchedEffect(key1 = checkedBgColor, key2 = checked, block = {
        if (checked) {
            bgAnimator.launchAnimateTo(this, checkedBgColor)
            thumbAnimator.launchAnimateTo(this, checkedThumbColor)
            thumbOffsetAnimator.launchAnimateTo(this, 26f, 300)
        } else {
            bgAnimator.launchAnimateTo(this, uncheckedBgColor)
            thumbAnimator.launchAnimateTo(this, uncheckedThumbColor)
            thumbOffsetAnimator.launchAnimateTo(this, 0f, 300)
        }
    })

    Box(
        Modifier
            .size(56.dp, 30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(bgAnimator.value)
            .clickable { onCheckedChange() }
    ) {
        Box(
            Modifier
                .offset(thumbOffsetAnimator.value.dp)
                .padding(4.dp)
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(thumbAnimator.value)
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