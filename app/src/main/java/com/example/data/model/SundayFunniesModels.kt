package com.example.data.model

data class ComicStripItem(
    val id: String,
    val title: String,
    val syndicateSeries: String, // e.g. "The Townsquare Pressroom", "Press & Pixel", "Metro Minutes"
    val cartoonistName: String,
    val publishDateFormatted: String,
    val imageUrl: String,
    val caption: String,
    val issueNumber: Int,
    val laughsCount: Int = 0,
    val spotOnCount: Int = 0,
    val classicCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isUserSubmittedMeme: Boolean = false,
    val tags: List<String> = emptyList()
)
