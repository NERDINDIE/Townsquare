package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NewsblogCategory(val displayName: String, val badgeEmoji: String, val colorHex: Long) {
    ALL("All Updates", "⚡", 0xFF00D2FF),
    BREAKING("Breaking", "🔴", 0xFFFF3B30),
    COUNCIL("City Council", "🏛️", 0xFF00E676),
    TRANSIT("Transit & Rail", "🚇", 0xFFFF9500),
    CULTURE("Culture & Arts", "🎭", 0xFFAF52DE),
    WEATHER("Atmospheric", "⛈️", 0xFF5856D6),
    CIVIC("Civic Patrol", "📢", 0xFFFF2D55)
}

enum class NewsblogUrgency(val label: String, val colorHex: Long) {
    CRITICAL("CRITICAL BREAKING", 0xFFFF3B30),
    HIGH("DEVELOPING WIRE", 0xFFFF9500),
    STANDARD("CIVIC DISPATCH", 0xFF00D2FF),
    ANALYSIS("MINUTE ANALYSIS", 0xFF34C759)
}

@Entity(tableName = "live_newsblog_entries")
data class LiveNewsblogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val headline: String,
    val body: String,
    val authorName: String,
    val authorRole: String = "Civic Wire Desk",
    val authorAvatarUrl: String = "",
    val categoryTag: String = "BREAKING", // from NewsblogCategory.name
    val urgencyLevel: String = "HIGH", // from NewsblogUrgency.name
    val timestampFormatted: String, // e.g. "23:18 EDT"
    val timestampMillis: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val keyTakeaway: String = "",
    val quote: String = "",
    val quoteSpeaker: String = "",
    val location: String = "City Center",
    val imageResName: String = "",
    val likesCount: Int = 14,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val audioSnippetTitle: String = "",
    val audioSnippetDurationSec: Int = 0,
    val verifiedSourcesCount: Int = 3
)
