package com.example.ui

data class AppTab(
    val id: String,
    val title: String,
    val navIndex: Int
)

object NavDestination {
    const val FEED = 0
    const val NEWSBLOG = 1
    const val VISUAL_GALLERY = 2
    const val TV_STREAMING = 3
    const val NEWSSTAND = 4
    const val JOURNAL = 5
    const val AUDIO_HUB = 6
    const val SPACES = 7
    const val COMMUNITY = 8
    const val FUNNIES = 9
    const val PARTNERS = 10
    const val DISCOVERY = 11
    const val PLAYGROUND = 12
    const val ENGAGEMENT_DASHBOARD = 13
    const val BROWSER = 14
    const val TOWNSQUARE_PLUS = 15
    const val PLUS_EXTENSIONS = 16
    const val PLUS_FINANCE = 17
    const val PLUS_CATALOGS = 18
    const val PLUS_STATE = 19
}
