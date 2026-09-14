package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VisualCategory(val displayName: String, val emoji: String) {
    ALL("All Visuals", "✨"),
    PHOTOJOURNALISM("Photojournalism", "📸"),
    ARCHITECTURE("Civic Architecture", "🏛️"),
    STREET("Street Life", "🚶"),
    PORTRAITS("Portraits & Faces", "👤"),
    DOCUMENTARY("Documentary Essay", "🎞️"),
    NIGHTLIFE("Culture & Nightlife", "🌃")
}

@Entity(tableName = "visual_posts")
data class VisualPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val caption: String,
    val photographerName: String,
    val photographerHandle: String = "@townsquare_photo",
    val photographerAvatarUrl: String = "",
    val category: String = "Photojournalism", // from VisualCategory.displayName
    val imageResUrl: String = "", // high-res photo url
    val aspectRatio: Float = 1.33f, // width / height
    val locationTaken: String = "Central Plaza",
    val cameraMeta: String = "Leica M11 • 35mm f/1.4 Summilux • ISO 200 • 1/500s",
    val storyContext: String = "",
    val multiPhotoUrls: String = "", // comma-separated photo URLs for carousel
    val tags: String = "Visual,Photography,Civic",
    val likesCount: Int = 142,
    val commentsCount: Int = 18,
    val viewsCount: Int = 2450,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val timestampFormatted: String = "2 hours ago",
    val timestampMillis: Long = System.currentTimeMillis()
)
