package ru.debajo.reader.rss.ui.common

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

@Composable
fun <T> rememberMutableState(initialState: T): MutableState<T> {
    return remember { mutableStateOf(initialState) }
}

@Composable
fun <T> rememberSaveableMutableState(initialState: T): MutableState<T> {
    return rememberSaveable { mutableStateOf(initialState) }
}

@Composable
fun rememberEnterAlwaysScrollBehavior(
    topAppBarScrollState: TopAppBarState = rememberTopAppBarState()
): TopAppBarScrollBehavior {
    val flingAnimationSpec = rememberSplineBasedDecay<Float>()

    return remember(topAppBarScrollState, flingAnimationSpec) {
        EnterAlwaysScrollBehavior(topAppBarScrollState, flingAnimationSpec)
    }
}

private class EnterAlwaysScrollBehavior(
    override var state: TopAppBarState,
    val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!canScroll()) return Offset.Zero
                val prevHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return if (prevHeightOffset != state.heightOffset) {
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y
                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                    if (consumed.y == 0f && available.y > 0f) {
                        state.contentOffset = 0f
                    }
                }
                state.heightOffset = state.heightOffset + consumed.y
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                var result = super.onPostFling(consumed, available)
                if (state.collapsedFraction > 0.01f && state.collapsedFraction < 1f) {
                    result += flingTopAppBar(
                        state = state,
                        initialVelocity = available.y,
                        flingAnimationSpec = flingAnimationSpec
                    )
                    snapTopAppBar(state)
                }
                return result
            }
        }
}

private suspend fun flingTopAppBar(
    state: TopAppBarState,
    initialVelocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?
): Velocity {
    var remainingVelocity = initialVelocity
    if (flingAnimationSpec != null && abs(initialVelocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    return Velocity(0f, remainingVelocity)
}

private suspend fun snapTopAppBar(state: TopAppBarState) {
    if (state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(initialValue = state.heightOffset).animateTo(
            if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) { state.heightOffset = value }
    }
}