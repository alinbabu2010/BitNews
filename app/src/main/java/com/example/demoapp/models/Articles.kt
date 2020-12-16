package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for each news article
 */
@Parcelize
data class Articles(
    @SerializedName("source") val source: Source? = Source(),
    @SerializedName("author") val author: String? = "",
    @SerializedName("title") val title: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("url") val url: String? = "",
    @SerializedName("urlToImage") val imageUrl: String? = "",
    @SerializedName("publishedAt") val publishedDate: String? = "",
    @SerializedName("content") val content: String? = ""
) : Parcelable
