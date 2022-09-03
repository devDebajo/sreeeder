package ru.debajo.reader.rss.data.remote.load.ext

import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine {
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    it.resume(response, null)
                } catch (e: Throwable) {
                    it.resumeWithException(e)
                }
            }

        })

        it.invokeOnCancellation { this@await.cancel() }
    }
}
