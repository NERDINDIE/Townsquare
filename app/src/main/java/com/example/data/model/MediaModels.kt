package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MediaType(val displayName: String, val badge: String) {
    SOCIAL_POST("Social", "💬 Social"),
    NEWSPAPER_MAGAZINE("Press & Mags", "📰 Magazine"),
    NEWSLETTER("Newsletter", "✉️ Newsletter"),
    RADIO_STATION("Radio Live", "📻 Live Radio"),
    PODCAST_EPISODE("Podcast", "🎙️ Podcast")
}

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // from MediaType.name
    val title: String,
    val subtitle: String = "",
    val authorName: String,
    val authorHandle: String = "",
    val channelId: String,
    val channelName: String,
    val spaceId: Long? = null,
    val spaceTitle: String? = null,
    val bodyText: String,
    val mediaUrl: String = "",
    val imageResName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val readTimeMinutes: Int = 0,
    val durationSeconds: Int = 0,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val isSavedOffline: Boolean = false,
    val isUserCreated: Boolean = false,
    val tags: String = "", // comma-separated tags
    val stationFrequency: String = "", // e.g. "98.5 FM"
    val issueEdition: String = "" // e.g. "Vol. 28 • Morning Edition"
)

@Entity(tableName = "media_channels")
data class MediaChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val bannerColorHex: Long,
    val isFollowed: Boolean = true,
    val followersCount: Int = 1250,
    val morningBriefHighlight: String = "",
    val iconEmoji: String = "📡"
)

@Entity(tableName = "media_spaces")
data class MediaSpaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val handle: String,
    val description: String,
    val category: String,
    val subscriberCount: Int = 1,
    val accentColorHex: Long = 0xFF00D2FF,
    val isOwner: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)

data class MorningBriefHighlight(
    val channelName: String,
    val topicHeadline: String,
    val summary: String,
    val mediaItemId: Long? = null,
    val category: String = "Top Story"
)

@Entity(tableName = "journal_editions")
data class JournalEditionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val newspaperTitle: String, // e.g. "The Townsquare Chronicle"
    val motto: String = "All the Town's News in Print",
    val volumeNumber: Int = 1,
    val issueNumber: Int = 1,
    val issueDate: String = "Thursday, Sept 10, 2026",
    val templateStyle: String = "CLASSIC_BROADSHEET", // CLASSIC_BROADSHEET, MODERN_GAZETTE, VINTAGE_PRESS, EXECUTIVE_DISPATCH
    val bannerColorHex: Long = 0xFFD4A373,
    val leadHeadline: String,
    val leadSubheadline: String = "",
    val leadArticleBody: String,
    val leadAuthor: String = "Editor in Chief",
    val secondaryHeadline: String = "",
    val secondaryArticleBody: String = "",
    val editorialNotes: String = "", // Note from the Publisher
    val communityBulletin: String = "", // Notices, local weather, quotes
    val circulationReads: Int = 48,
    val isArchived: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)

data class FlipbookPageData(
    val pageNumber: Int,
    val totalPages: Int,
    val sectionHeader: String,
    val mastheadTitle: String,
    val issueEdition: String,
    val dateString: String,
    val headline: String,
    val subheadline: String = "",
    val author: String = "",
    val paragraphs: List<String>,
    val pullQuote: String = "",
    val pullQuoteAuthor: String = "",
    val photoCaption: String = "",
    val bulletPoints: List<String> = emptyList(),
    val templateStyle: String = "CLASSIC_BROADSHEET",
    val accentColorHex: Long = 0xFF00D2FF
)

enum class BulletinCategory(val displayName: String, val emoji: String, val colorHex: Long) {
    TRAFFIC("Traffic & Transit", "🚦", 0xFFFF9100),
    WEATHER("Local Weather", "⛈️", 0xFF00D2FF),
    EVENT("Civic & Culture", "🎪", 0xFF00E676),
    ALERT("Safety & Advisory", "⚠️", 0xFFFF5252),
    COMMUNITY("Neighborhood", "📢", 0xFFB388FF)
}

enum class BulletinUrgency(val label: String, val colorHex: Long) {
    INFO("Info", 0xFF00D2FF),
    MODERATE("Active Notice", 0xFFFFB300),
    URGENT("Urgent Alert", 0xFFFF5252)
}

@Entity(tableName = "local_bulletins")
data class LocalBulletinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String = "TRAFFIC", // from BulletinCategory.name
    val title: String,
    val description: String,
    val locationName: String,
    val reporterName: String = "Community Reporter",
    val reporterHandle: String = "@civic_patrol",
    val timestamp: Long = System.currentTimeMillis(),
    val upvotesCount: Int = 1,
    val isUpvoted: Boolean = false,
    val urgencyLevel: String = "INFO", // from BulletinUrgency.name
    val iconEmoji: String = "🚦",
    val status: String = "ACTIVE", // ACTIVE, VERIFIED, RESOLVED
    val isUserSubmitted: Boolean = false
)
