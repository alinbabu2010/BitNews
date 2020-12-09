package com.example.demoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for each news article
 */
@Parcelize
data class Articles(
    var source: Source? = null,
    var author: Source? = null,
    var title: Source? = null,
    var description: Source? = null,
    var url: Source? = null,
    var urlToImage: Source? = null,
    var publishedAt: Source? = null,
    var content: Source? = null
) : Parcelable
