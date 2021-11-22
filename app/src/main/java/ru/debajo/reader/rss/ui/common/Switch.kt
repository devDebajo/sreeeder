package ru.debajo.reader.rss.ui.common

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    val checkedBgColor = MaterialTheme.colorScheme.primaryContainer
    val uncheckedBgColor = MaterialTheme.colorScheme.secondary
    val checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer
    val uncheckedThumbColor = MaterialTheme.colorScheme.onSecondary

    val bgAnimator = remember(uncheckedBgColor) { Animatable(uncheckedBgColor) }
    val thumbAnimator = remember(uncheckedThumbColor) { Animatable(uncheckedThumbColor) }
    val thumbOffsetAnimator = remember { Animatable(0f) }
    LaunchedEffect(key1 = checkedBgColor, block = {
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
): Job {
    return scope.launch { animateTo(targetValue, tween(durationMs)) }
}