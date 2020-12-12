package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for source of news
 */
@Parcelize
data class Source(
    @SerializedName("sourceId") var id: String? = null,
    @SerializedName("sourceName") var name: String? = null
) : Parcelable
