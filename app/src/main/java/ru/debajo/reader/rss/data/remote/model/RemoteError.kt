package ru.debajo.reader.rss.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoteError(
    @SerializedName("id")
    val id: String,

    @SerializedName("stackTrace")
    val stackTrace: String,

    @SerializedName("message")
    val message: String?,

    @SerializedName("customMessage")
    val customMessage: String?,

    @SerializedName("tag")
    val tag: String?,

    @SerializedName("dateUtc")
    val dateUtc: Long,

    @SerializedName("appVersion")
    val appVersion: String,

    @SerializedName("sdkVersion")
    val sdkVersion: Int,

    @SerializedName("fatal")
    val fatal: Boolean,
) : Serializable
