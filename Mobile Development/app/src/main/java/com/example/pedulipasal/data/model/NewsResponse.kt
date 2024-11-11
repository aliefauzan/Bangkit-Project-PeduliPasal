package com.example.pedulipasal.data.model


data class NewsResponse(
    val totalResults: Int? = null,
    val articles: List<NewsItem?>? = null,
    val status: String? = null
)

data class Source(
    val name: String? = null,
    val id: Any? = null
)

data class NewsItem(
    val publishedAt: String? = null,
    val author: Any? = null,
    val urlToImage: String? = null,
    val description: String? = null,
    val source: Source? = null,
    val title: String? = null,
    val url: String? = null,
    val content: String? = null
)
