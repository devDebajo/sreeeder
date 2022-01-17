package ru.debajo.reader.rss.ui.common

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AppImage(
    modifier: Modifier = Modifier,
    url: String?,
    builder: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this },
    onFailure: @Composable (BoxScope.() -> Unit)? = null,
    appearAnimation: Boolean = true,
    shimmer: Boolean = true
) {
    val circularReveal = remember(url, appearAnimation) {
        if (appearAnimation) CircularReveal(duration = 250) else null
    }
    if (shimmer) {
        GlideImage(
            imageModel = url,
            circularReveal = circularReveal,
            shimmerParams = ShimmerParams(
                baseColor = MaterialTheme.colorScheme.background,
                highlightColor = Color.White,
                durationMillis = 350,
                dropOff = 0.65f,
                tilt = 20f
            ),
            failure = if (onFailure == null) {
                null
            } else {
                { onFailure() }
            },
            modifier = modifier,
            requestBuilder = { Glide.with(LocalContext.current).asDrawable().builder() }
        )
    } else{
        GlideImage(
            imageModel = url,
            circularReveal = circularReveal,
            failure = if (onFailure == null) {
                null
            } else {
                { onFailure() }
            },
            modifier = modifier,
            requestBuilder = { Glide.with(LocalContext.current).asDrawable().builder() }
        )
    }
}
