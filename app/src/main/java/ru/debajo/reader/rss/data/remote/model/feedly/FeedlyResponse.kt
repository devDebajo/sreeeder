package ru.debajo.reader.rss.data.remote.model.feedly

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedlyResponse(
    @SerializedName("results")
    val results: List<FeedlySearchMatch>
) : Serializable
