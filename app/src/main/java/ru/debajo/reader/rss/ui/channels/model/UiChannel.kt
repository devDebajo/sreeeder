package ru.debajo.reader.rss.ui.channels.model

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Stable

@Stable
data class UiChannel(
    val url: UiChannelUrl,
    val name: String,
    val image: String?,
    val description: String?,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        UiChannelUrl(parcel.readString()!!),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url.url)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UiChannel> {
        override fun createFromParcel(parcel: Parcel): UiChannel = UiChannel(parcel)

        override fun newArray(size: Int): Array<UiChannel?> = arrayOfNulls(size)
    }
}
