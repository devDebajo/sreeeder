package ru.debajo.reader.rss.data

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun loadImage(context: Context, url: String): Bitmap? {
    return suspendCancellableCoroutine {
        val future = Glide.with(context)
            .asBitmap()
            .load(url)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    it.resume(null, null)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    it.resume(resource, null)
                    return false
                }

            })
            .submit()

        it.invokeOnCancellation { future.cancel(true) }
    }
}