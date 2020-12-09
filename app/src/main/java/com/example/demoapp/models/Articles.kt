package com.example.demoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for each news article
 */
@Parcelize
data class Articles(
    var source: Source? = null,
    var author: String? = null,
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var urlToImage: String? = null,
    var publishedAt: String? = null,
    var content: String? = null
) : Parcelable
