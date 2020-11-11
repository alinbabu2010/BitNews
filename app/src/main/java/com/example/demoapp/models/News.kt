package com.example.demoapp.models

/**
 * Data class for news
 */
data class News(val articles: ArrayList<Articles>)

/**
 * Data class for each news article
 */
data class Articles(
    var source: Source,
    var author: String,
    var title: String,
    var description: String,
    var url: String,
    var urlToImage: String,
    var publishedAt: String,
    var content: String
)

/**
 * Data class for source of news
 */
data class Source(
    var id: Int,
    var name: String
)