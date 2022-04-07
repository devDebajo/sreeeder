package ru.debajo.reader.rss.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.min
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.ui.ext.toPx
import kotlin.math.abs

@Composable
fun IndeterminateProgressIndicator(
    modifier: Modifier = Modifier,
    inProgress: Boolean = true,
    color: Color = Color.Black,
) {
    val easing = remember { decelerateEasing() }
    val rotationAnimator = remember { Animatable(0f) }
    val alphaAnimator = remember { Animatable(1f) }
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing))
    )
    LaunchedEffect(key1 = rotationAnimator, key2 = inProgress, block = {
        if (inProgress) {
            val duration = (1650 + abs(70f * 10 - rotationAnimator.value * 10) / 2).toLong()
            rotationAnimator.animateTo(
                targetValue = 70f,
                animationSpec = tween(duration.toInt(), easing = easing)
            )
            rotationAnimator.animateTo(
                targetValue = 99f,
                animationSpec = tween(30000, easing = easing)
            )
        } else {
            launch {
                rotationAnimator.animateTo(
                    targetValue = 100f,
                    animationSpec = tween(easing = easing)
                )
            }
            launch {
                alphaAnimator.animateTo(
                    targetValue = 0f
                )
            }
        }
    })

    if (alphaAnimator.value > 0f) {
        BoxWithConstraints(modifier.alpha(alphaAnimator.value)) {
            val size = min(minWidth, minHeight)
            val stroke = size * 0.07f
            val strokePx = stroke.toPx()
            Canvas(
                Modifier
                    .size(size)
                    .padding(stroke / 2f)
                    .align(Alignment.Center)
            ) {
                withTransform({
                    rotate(rotation)
                }) {
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360 * (rotationAnimator.value / 100f),
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
            }
        }
    }
}
