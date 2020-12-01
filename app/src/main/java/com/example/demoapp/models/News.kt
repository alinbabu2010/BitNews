package com.example.demoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class for news
 */
@Parcelize
data class News(val articles: ArrayList<Articles>) : Parcelable

/**
 * Data class for each news article
 */
@Parcelize
data class Articles(
    var source: Source,
    var author: String,
    var title: String,
    var description: String,
    var url: String,
    var urlToImage: String,
    var publishedAt: String,
    var content: String
) : Parcelable

/**
 * Data class for source of news
 */
@Parcelize
data class Source(
    var id: String?,
    var name: String
) : Parcelable