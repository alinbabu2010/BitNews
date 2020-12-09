package com.example.demoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for source of news
 */
@Parcelize
data class Source(
    var id: String? = null,
    var name: String? = null
) : Parcelable
