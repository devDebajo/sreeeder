package ru.debajo.reader.rss.ui.ext

import androidx.compose.animation.core.*
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset

suspend fun MutableState<Float>.animateTo(
    targetValue: Float,
    initialVelocity: Float = 0f,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    animateTo(
        typeConverter = Float.VectorConverter,
        targetValue = targetValue,
        animationSpec = animationSpec,
        initialVelocity = initialVelocity,
    )
}

suspend fun MutableState<Offset>.animateTo(
    targetValue: Offset,
    initialVelocity: Offset = Offset.Zero,
    animationSpec: AnimationSpec<Offset> = spring(),
) {
    animateTo(
        typeConverter = Offset.VectorConverter,
        targetValue = targetValue,
        animationSpec = animationSpec,
        initialVelocity = initialVelocity,
    )
}

suspend fun <T, V : AnimationVector> MutableState<T>.animateTo(
    typeConverter: TwoWayConverter<T, V>,
    targetValue: T,
    initialVelocity: T? = null,
    animationSpec: AnimationSpec<T> = spring(),
) {
    animate(
        typeConverter = typeConverter,
        targetValue = targetValue,
        initialValue = value,
        animationSpec = animationSpec,
        initialVelocity = initialVelocity,
        block = { value, _ -> this.value = value }
    )
}
