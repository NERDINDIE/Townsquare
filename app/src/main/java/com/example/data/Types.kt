package com.example.data

import java.time.LocalDateTime

/**
 * Converted from src/lib/types.ts
 */
data class Article(
    val id: String,
    val slug: String,
    val title: String,
    val author: String,
    val date: String,
    val image: String,
    val category: String,
    val excerpt: String,
    val content: String,
    val featured: Boolean? = null,
    val videoUrl: String? = null
)
