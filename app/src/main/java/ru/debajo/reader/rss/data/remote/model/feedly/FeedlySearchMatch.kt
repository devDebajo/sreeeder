package ru.debajo.reader.rss.data.remote.model.feedly

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FeedlySearchMatch(
    @SerializedName("contentType")
    val contentType: FeedlyContentType?,

    @SerializedName("feedId")
    val feedId: String?
) : Serializable
