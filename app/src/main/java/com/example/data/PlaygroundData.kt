package com.example.data

/**
 * Converted from src/lib/playground-data.ts
 */
data class PlaygroundArticle(
    val title: String,
    val excerpt: String,
    val image: String
)

data class PlaygroundItem(
    val title: String,
    val icon: String? = null,
    val image: String? = null,
    val duration: String? = null,
    val thumbnail: String? = null
)

data class PlaygroundContent(
    val articles: List<PlaygroundArticle>,
    val games: List<PlaygroundItem>,
    val comics: List<PlaygroundItem>,
    val videos: List<PlaygroundItem>
)

val playgroundContent = PlaygroundContent(
    articles = listOf(
        PlaygroundArticle(
            title = "The Magical Treehouse Adventure",
            excerpt = "Join Lily and Tom as they discover a secret, magical treehouse in their backyard and embark on an unforgettable journey!",
            image = "https://placehold.co/600x400.png"
        )
    ),
    games = listOf(
        PlaygroundItem(title = "Shape Sorter", icon = "🔺"),
        PlaygroundItem(title = "Animal Sounds", icon = "🦁"),
        PlaygroundItem(title = "Color Match", icon = "🎨"),
        PlaygroundItem(title = "Puzzler", icon = "🧩")
    ),
    comics = listOf(
        PlaygroundItem(title = "Super Squirrel: The Acorn Thief", image = "https://placehold.co/300x450.png"),
        PlaygroundItem(title = "The Adventures of Captain Comet", image = "https://placehold.co/300x450.png"),
        PlaygroundItem(title = "Dino-Mite Explorers", image = "https://placehold.co/300x450.png"),
        PlaygroundItem(title = "The Mystery of the Missing Toy", image = "https://placehold.co/300x450.png")
    ),
    videos = listOf(
        PlaygroundItem(title = "Learn to Count with Fun Fruits", duration = "3:45", thumbnail = "https://placehold.co/400x225.png"),
        PlaygroundItem(title = "Sing the Alphabet Song!", duration = "2:30", thumbnail = "https://placehold.co/400x225.png"),
        PlaygroundItem(title = "How to Draw a Friendly Dinosaur", duration = "5:10", thumbnail = "https://placehold.co/400x225.png")
    )
)
