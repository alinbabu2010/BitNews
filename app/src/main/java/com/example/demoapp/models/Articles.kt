package com.example.demoapp.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for each news article
 */
@Entity
@Parcelize
data class Articles(
    @Embedded @SerializedName("source") val source: Source? = Source(),
    @SerializedName("author") val author: String? = "",
    @PrimaryKey @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("url") val url: String? = "",
    @SerializedName("urlToImage") val imageUrl: String? = "",
    @SerializedName("publishedAt") val publishedDate: String? = "",
    @SerializedName("content") val content: String? = ""
) : Parcelable
