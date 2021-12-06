package ru.debajo.reader.rss.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Query
import ru.debajo.reader.rss.data.remote.model.feedly.FeedlyResponse

interface FeedlyService {
    @GET("/v3/search/feeds")
    suspend fun search(
        @Query("q") query: String,
        @Query("n") limit: Int,
    ): FeedlyResponse
}
