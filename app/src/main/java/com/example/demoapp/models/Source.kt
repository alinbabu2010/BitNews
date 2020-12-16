package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for source of news
 */
@Parcelize
data class Source(
    @SerializedName("id") val id: String? = "",
    @SerializedName("name") val name: String? = ""
) : Parcelable
