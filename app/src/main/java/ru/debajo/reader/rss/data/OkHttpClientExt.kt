package ru.debajo.reader.rss.data

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.debajo.reader.rss.data.remote.load.ext.await

suspend fun OkHttpClient.getRawText(url: String): String {
    val response = getResponse(url)
    return withContext(IO) {
        response.body?.byteStream()?.bufferedReader()?.use { it.readText() }.orEmpty()
    }
}

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun OkHttpClient.getBytes(url: String): ByteArray {
    val response = getResponse(url)
    return withContext(IO) {
        response.body?.bytes()!!
    }
}

suspend fun OkHttpClient.getResponse(url: String): Response {
    return withContext(IO) {
        val request = Request.Builder().get().url(url).build()
        newCall(request).await()
    }
}