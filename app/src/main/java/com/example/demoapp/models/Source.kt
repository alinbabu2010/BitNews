package com.example.demoapp.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class for source of news
 */
@Parcelize
data class Source(
    @ColumnInfo(name = "source_id") @SerializedName("id") val id: String? = "",
    @ColumnInfo(name = "source_name") @SerializedName("name") val name: String? = ""
) : Parcelable
