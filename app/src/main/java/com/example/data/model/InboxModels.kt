package com.example.data.model

enum class InboxCategory(val label: String, val emoji: String) {
    ALL("All", "📬"),
    BRIEFS("Briefs", "☀️"),
    ALERTS("Alerts", "🚨"),
    DISPATCHES("Dispatches", "📍"),
    EDITORIAL("Editorial", "✍️")
}

enum class InboxActionType {
    OPEN_BRIEF,
    PLAY_BRIEF_AUDIO,
    OPEN_ARTICLE,
    OPEN_BULLETIN,
    OPEN_WEATHER,
    NONE
}

data class InboxNotificationItem(
    val id: String,
    val category: InboxCategory,
    val title: String,
    val snippet: String,
    val timestamp: Long,
    val timeAgo: String,
    val sourceLabel: String,
    val badgeEmoji: String,
    val accentColorHex: Long = 0xFF00D2FF,
    val isRead: Boolean = false,
    val hasAudio: Boolean = false,
    val actionType: InboxActionType = InboxActionType.NONE,
    val targetId: String? = null
)

object InboxSeedHelper {
    fun getInitialInboxItems(): List<InboxNotificationItem> {
        val now = System.currentTimeMillis()
        return listOf(
            InboxNotificationItem(
                id = "inbox_brief_today",
                category = InboxCategory.BRIEFS,
                title = "Your Morning Brief: Across 5 Followed Channels",
                snippet = "Global central banks hold rates steady as tech sector productivity reaches a 5-year high. Plus civic transit updates and weekend outlook.",
                timestamp = now - 35 * 60 * 1000,
                timeAgo = "35m ago",
                sourceLabel = "Townsquare Morning Desk",
                badgeEmoji = "☀️",
                accentColorHex = 0xFFFFB300,
                isRead = false,
                hasAudio = true,
                actionType = InboxActionType.OPEN_BRIEF
            ),
            InboxNotificationItem(
                id = "inbox_breaking_transit",
                category = InboxCategory.ALERTS,
                title = "Breaking: Metro Transit Line 4 Resumes Full Schedule",
                snippet = "Transit Authority confirms signaling updates completed ahead of schedule. Normal 6-minute rush hour headways restored.",
                timestamp = now - 15 * 60 * 1000,
                timeAgo = "15m ago",
                sourceLabel = "Civic Wire Dispatch",
                badgeEmoji = "🚨",
                accentColorHex = 0xFFFF3B30,
                isRead = false,
                hasAudio = false,
                actionType = InboxActionType.OPEN_ARTICLE
            ),
            InboxNotificationItem(
                id = "inbox_dispatch_parkway",
                category = InboxCategory.DISPATCHES,
                title = "Traffic Advisory: Riverfront Parkway Lane Restriction",
                snippet = "Single lane reduction near Pier 3 due to scheduled bridge expansion survey. 10-15 minute delays expected.",
                timestamp = now - 50 * 60 * 1000,
                timeAgo = "50m ago",
                sourceLabel = "Transit Officer Dan",
                badgeEmoji = "🚧",
                accentColorHex = 0xFFF59E0B,
                isRead = false,
                hasAudio = false,
                actionType = InboxActionType.OPEN_BULLETIN
            ),
            InboxNotificationItem(
                id = "inbox_weather_bulletin",
                category = InboxCategory.DISPATCHES,
                title = "Weather Advisory: Passing Fog Lifting to 72°F Partly Sunny",
                snippet = "Gentle southwesterly breeze with favorable clean air index (AQI 35). Afternoon temperatures peaking around 77°F.",
                timestamp = now - 90 * 60 * 1000,
                timeAgo = "1h ago",
                sourceLabel = "Civic Weather Station",
                badgeEmoji = "⛅",
                accentColorHex = 0xFF00D2FF,
                isRead = true,
                hasAudio = false,
                actionType = InboxActionType.OPEN_WEATHER
            ),
            InboxNotificationItem(
                id = "inbox_editorial_letter",
                category = InboxCategory.EDITORIAL,
                title = "Letters to Editor: Municipal Energy Surplus Response",
                snippet = "Editorial Director Helen Vance has replied to your civic feedback regarding public solar cooperative grants.",
                timestamp = now - 3 * 3600 * 1000,
                timeAgo = "3h ago",
                sourceLabel = "Townsquare Editorial Board",
                badgeEmoji = "✍️",
                accentColorHex = 0xFFA855F7,
                isRead = true,
                hasAudio = false,
                actionType = InboxActionType.NONE
            )
        )
    }
}
