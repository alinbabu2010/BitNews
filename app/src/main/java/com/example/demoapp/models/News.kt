package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for news
 */
@Parcelize
data class News(
    @SerializedName("status") val status: String? = null,
    @SerializedName("totalResults") val totalResults: Int = 20,
    @SerializedName("articles") val articles: ArrayList<Articles>
) : Parcelable