package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for each news article
 */
@Parcelize
data class Articles(
    @SerializedName("Source") var source: Source? = null,
    @SerializedName("author") var author: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("urlToImage") var imageUrl: String? = null,
    @SerializedName("publishedAt") var publishedDate: String? = null,
    @SerializedName("content") var content: String? = null
) : Parcelable
