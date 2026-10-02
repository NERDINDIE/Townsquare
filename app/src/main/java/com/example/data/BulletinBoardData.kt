package com.example.data

/**
 * Converted from src/lib/bulletin-board-data.ts
 */
data class Post(
    val id: String,
    val time: String,
    val content: String,
    val image: String? = null,
    val dataAiHint: String? = null,
    val images: List<PostImage>? = null,
    val likes: Int,
    val comments: Int,
    val location: PostLocation? = null
)

data class PostImage(
    val src: String,
    val hint: String
)

data class PostLocation(
    val name: String,
    val lat: Double,
    val lng: Double
)

data class User(
    val handle: String,
    val name: String,
    val avatar: String,
    val fallback: String,
    val posts: List<Post>
)

data class PostAuthor(
    val handle: String,
    val name: String,
    val avatar: String,
    val fallback: String
)

data class PostWithAuthor(
    val id: String,
    val time: String,
    val content: String,
    val image: String? = null,
    val dataAiHint: String? = null,
    val images: List<PostImage>? = null,
    val likes: Int,
    val comments: Int,
    val location: PostLocation? = null,
    val author: PostAuthor
)

val historicalUsers: List<User> = emptyList()
val literaryUsers: List<User> = emptyList()
val regularUsers: List<User> = emptyList()
val users: List<User> = historicalUsers + literaryUsers + regularUsers

fun getTimeValue(time: String): Long {
    return try {
        when {
            time == "ALWAYS" || time == "CONSTANTLY" -> System.currentTimeMillis()
            time.contains("h ago") -> {
                val hours = time.substringBefore(' ').toInt()
                System.currentTimeMillis() - hours * 60L * 60L * 1000L
            }
            time.contains("d ago") -> {
                val days = time.substringBefore(' ').toInt()
                System.currentTimeMillis() - days * 24L * 60L * 60L * 1000L
            }
            else -> 0L
        }
    } catch (_: Exception) {
        0L
    }
}
