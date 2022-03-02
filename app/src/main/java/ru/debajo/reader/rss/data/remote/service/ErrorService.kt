package ru.debajo.reader.rss.data.remote.service

import retrofit2.http.Body
import retrofit2.http.POST
import ru.debajo.reader.rss.data.remote.model.RemoteError

interface ErrorService {
    @POST("persistErrors")
    suspend fun send(@Body errors: List<RemoteError>)
}
