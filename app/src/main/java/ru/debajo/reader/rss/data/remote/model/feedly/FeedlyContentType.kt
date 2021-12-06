package ru.debajo.reader.rss.data.remote.model.feedly

import com.google.gson.annotations.SerializedName

enum class FeedlyContentType {
    @SerializedName("article")
    ARTICLE,

    @SerializedName("video")
    VIDEO
}
