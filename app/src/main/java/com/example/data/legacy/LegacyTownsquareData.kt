package com.example.data.legacy

/**
 * Legacy content models converted from the old Townsquare-Old TypeScript lib.
 * These are intentionally kept isolated in a dedicated package so they can be adopted
 * incrementally by the Android app without conflicting with the existing app model layer.
 */

data class LegacyArticle(
    val id: String,
    val slug: String,
    val title: String,
    val author: String,
    val date: String,
    val image: String,
    val category: String,
    val excerpt: String,
    val content: String,
    val featured: Boolean = false,
    val videoUrl: String? = null
)

data class LegacyBrand(
    val name: String,
    val slug: String,
    val description: String,
    val image: String,
    val dataAiHint: String
)

data class LegacyPost(
    val id: String,
    val time: String,
    val content: String,
    val image: String? = null,
    val dataAiHint: String? = null,
    val likes: Int = 0,
    val comments: Int = 0,
    val location: LegacyPostLocation? = null
)

data class LegacyPostLocation(
    val name: String,
    val lat: Double,
    val lng: Double
)

data class LegacyEdition(
    val id: String,
    val date: String,
    val coverImage: String
)

data class LegacyPublication(
    val id: String,
    val name: String,
    val slug: String,
    val logoText: String,
    val editions: List<LegacyEdition>
)

data class LegacyFirebaseConfig(
    val apiKey: String,
    val authDomain: String,
    val projectId: String,
    val storageBucket: String,
    val messagingSenderId: String,
    val appId: String
)

data class LegacyPlaygroundItem(
    val title: String,
    val icon: String? = null,
    val image: String? = null,
    val duration: String? = null,
    val thumbnail: String? = null
)

data class LegacySmsProvider(
    val id: Int,
    val name: String
)

data class LegacySmsMessage(
    val from: String,
    val content: String
)

data class LegacySmsThread(
    val id: Int,
    val provider: LegacySmsProvider,
    val timestamp: String,
    val messages: List<LegacySmsMessage>,
    val isSpam: Boolean = false,
    val name: String,
    val avatar: String,
    val fallback: String,
    val unread: Int,
    val type: String,
    val status: String
)

data class LegacyTownsquare(
    val name: String,
    val slug: String,
    val description: String,
    val image: String,
    val dataAiHint: String,
    val historicalHandles: List<String> = emptyList(),
    val nativeWordmark: String? = null
)

fun cn(vararg inputs: String?): String =
    inputs.filterNotNull().filter { it.isNotBlank() }.joinToString(" ")

val legacyBrands = listOf(
    LegacyBrand("Newsstand", "newsstand", "Your daily news roundup", "https://storage.googleapis.com/studioprompt-images/post-office-newsstand.jpg", "newspaper stand"),
    LegacyBrand("Bulletin Board", "bulletin-board", "Post and share with the community.", "https://placehold.co/400x900.png", "community bulletin board"),
    LegacyBrand("The Downtown Dish", "the-downtown-dish", "The best food and culture in town.", "https://placehold.co/400/900.png", "restaurant interior"),
    LegacyBrand("The Urbanist", "the-urbanist", "Exploring the city's architecture.", "https://placehold.co/400/900.png", "modern architecture")
)

val legacyArticles = listOf(
    LegacyArticle(
        id = "1",
        slug = "townsquare-times-your-daily-dose-of-local",
        title = "Townsquare Times: Your Daily Dose of Local",
        author = "Alex Doe",
        date = "October 26, 2023",
        image = "https://placehold.co/600x400.png",
        category = "Community",
        excerpt = "The latest happenings and stories from around our town.",
        content = "Welcome to the Townsquare Times, your number one source for all things local.",
        featured = true
    ),
    LegacyArticle(
        id = "2",
        slug = "culinary-renaissance-in-the-downtown-core",
        title = "A Culinary Renaissance in the Downtown Core",
        author = "John Smith",
        date = "October 25, 2023",
        image = "https://placehold.co/600x400.png",
        category = "Food & Culture",
        excerpt = "Discover the new wave of restaurants and chefs putting the city back on the map.",
        content = "Once a culinary desert, the city's downtown core is now experiencing a vibrant renaissance.",
        videoUrl = "https://example.com/video.mp4"
    )
)

val legacyPublications = listOf(
    LegacyPublication(
        id = "pub1",
        name = "Townsquare Daily",
        slug = "townsquare-daily",
        logoText = "TOWNSQUARE+DAILY",
        editions = listOf(
            LegacyEdition("ed1-1", "Saturday, August 16, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\nCover&font=roboto"),
            LegacyEdition("ed1-2", "Friday, August 15, 2025", "https://placehold.co/800x1067.png?text=Townsquare+Daily\nCover&font=roboto")
        )
    )
)

val legacyFirebaseConfig = LegacyFirebaseConfig(
    apiKey = "YOUR_API_KEY",
    authDomain = "YOUR_PROJECT_ID.firebaseapp.com",
    projectId = "YOUR_PROJECT_ID",
    storageBucket = "YOUR_PROJECT_ID.appspot.com",
    messagingSenderId = "YOUR_MESSAGING_SENDER_ID",
    appId = "YOUR_APP_ID"
)

val legacyPlaygroundItems = listOf(
    LegacyPlaygroundItem(title = "Shape Sorter", icon = "🔺"),
    LegacyPlaygroundItem(title = "Animal Sounds", icon = "🦁"),
    LegacyPlaygroundItem(title = "Color Match", icon = "🎨")
)

val legacySmsProviders = listOf(
    LegacySmsProvider(101, "Weather Alerts"),
    LegacySmsProvider(102, "NewsFlash"),
    LegacySmsProvider(103, "Townsquare Bank")
)

val legacySmsThreads = listOf(
    LegacySmsThread(
        id = 1,
        provider = legacySmsProviders[0],
        timestamp = "2:45 PM",
        messages = listOf(
            LegacySmsMessage("provider", "Hey, did you see the latest news about the downtown market?"),
            LegacySmsMessage("me", "No, what happened?")
        ),
        isSpam = false,
        name = "Emily White",
        avatar = "https://github.com/randomuser2.png",
        fallback = "EW",
        unread = 2,
        type = "human",
        status = "online"
    )
)

val legacyTownsquares = listOf(
    LegacyTownsquare(
        name = "Aethelgard",
        slug = "aethelgard",
        description = "A world of swords, sorcery, and summoned heroes.",
        image = "https://placehold.co/400x400.png",
        dataAiHint = "fantasy castle landscape",
        historicalHandles = listOf("isekai-hero-kaito", "demon-lord-valerius"),
        nativeWordmark = "Æthelgard"
    ),
    LegacyTownsquare(
        name = "Tokyo",
        slug = "tokyo",
        description = "A dazzling metropolis where tradition and future collide.",
        image = "https://placehold.co/400x400.png",
        dataAiHint = "tokyo street crossing",
        historicalHandles = listOf("kenji-tanaka", "haruto-ito"),
        nativeWordmark = "タウンスクエア"
    )
)
