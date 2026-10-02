package com.example.data.legacy

import com.example.data.model.JournalEditionEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity

/**
 * Bridge layer from the legacy Townsquare-Old TS data to the app's real Android models.
 *
 * The goal is to let old content be loaded incrementally without creating a second competing
 * data model system.
 */
object LegacyBridge {

    fun articleToMediaItem(
        article: LegacyArticle,
        channelId: String = "legacy-feed",
        channelName: String = "Legacy Feed"
    ): MediaItemEntity =
        MediaItemEntity(
            type = "SOCIAL_POST",
            title = article.title,
            subtitle = article.category,
            authorName = article.author,
            authorHandle = "@${article.slug}",
            channelId = channelId,
            channelName = channelName,
            bodyText = article.content,
            mediaUrl = article.videoUrl ?: "",
            imageResName = article.image,
            timestamp = System.currentTimeMillis(),
            readTimeMinutes = 3,
            durationSeconds = 0,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            isBookmarked = false,
            isSavedOffline = false,
            isUserCreated = true,
            tags = article.category,
            stationFrequency = "",
            issueEdition = article.category
        )

    fun articlesToMediaItems(
        articles: List<LegacyArticle>,
        channelId: String = "legacy-feed",
        channelName: String = "Legacy Feed"
    ): List<MediaItemEntity> =
        articles.map { articleToMediaItem(it, channelId, channelName) }

    fun brandToChannel(brand: LegacyBrand): MediaChannelEntity =
        MediaChannelEntity(
            id = brand.slug,
            name = brand.name,
            description = brand.description,
            category = "LEGACY",
            bannerColorHex = 0xFF00D2FF,
            isFollowed = true,
            followersCount = 1250,
            morningBriefHighlight = brand.description,
            iconEmoji = "📡"
        )

    fun brandsToChannels(brands: List<LegacyBrand>): List<MediaChannelEntity> =
        brands.map { brandToChannel(it) }

    fun publicationToJournalEdition(publication: LegacyPublication): JournalEditionEntity =
        JournalEditionEntity(
            newspaperTitle = publication.name,
            motto = "Legacy issue archive",
            volumeNumber = 1,
            issueNumber = 1,
            issueDate = publication.editions.firstOrNull()?.date ?: "Unknown issue date",
            templateStyle = "CLASSIC_BROADSHEET",
            bannerColorHex = 0xFFD4A373,
            leadHeadline = publication.name,
            leadSubheadline = publication.slug,
            leadArticleBody = publication.editions.firstOrNull()?.coverImage ?: "Legacy publication",
            leadAuthor = "Legacy Archive",
            secondaryHeadline = "",
            secondaryArticleBody = "",
            editorialNotes = "Migrated from Townsquare-Old library",
            communityBulletin = "",
            circulationReads = 1,
            isArchived = true,
            createdTimestamp = System.currentTimeMillis()
        )

    fun publicationsToJournalEditions(publications: List<LegacyPublication>): List<JournalEditionEntity> =
        publications.map { publicationToJournalEdition(it) }

    fun postToMediaItem(post: LegacyPost): MediaItemEntity =
        MediaItemEntity(
            type = "SOCIAL_POST",
            title = post.content.take(60).ifBlank { "Legacy Post" },
            subtitle = "Legacy community post",
            authorName = "Legacy User",
            authorHandle = "@legacy_user",
            channelId = "legacy-community",
            channelName = "Legacy Community",
            bodyText = post.content,
            mediaUrl = post.image ?: "",
            imageResName = post.image ?: "",
            timestamp = System.currentTimeMillis(),
            readTimeMinutes = 1,
            durationSeconds = 0,
            likesCount = post.likes,
            commentsCount = post.comments,
            sharesCount = 0,
            isLiked = false,
            isBookmarked = false,
            isSavedOffline = false,
            isUserCreated = true,
            tags = "legacy,community",
            stationFrequency = "",
            issueEdition = "community"
        )

    fun townsquareToChannel(townsquare: LegacyTownsquare): MediaChannelEntity =
        MediaChannelEntity(
            id = townsquare.slug,
            name = townsquare.name,
            description = townsquare.description,
            category = "TOWNSQUARE",
            bannerColorHex = 0xFF00D2FF,
            isFollowed = true,
            followersCount = 1000,
            morningBriefHighlight = townsquare.description,
            iconEmoji = "🏙️"
        )

    fun townsquaresToChannels(townsquares: List<LegacyTownsquare>): List<MediaChannelEntity> =
        townsquares.map { townsquareToChannel(it) }

    fun playgroundItemsToMediaItems(items: List<LegacyPlaygroundItem>): List<MediaItemEntity> =
        items.mapIndexed { index, item ->
            MediaItemEntity(
                type = "SOCIAL_POST",
                title = item.title,
                subtitle = "Playground",
                authorName = "Legacy Playground",
                authorHandle = "@playground",
                channelId = "legacy-playground",
                channelName = "Legacy Playground",
                bodyText = item.title,
                mediaUrl = item.thumbnail ?: item.image ?: "",
                imageResName = item.image ?: item.thumbnail ?: "",
                timestamp = System.currentTimeMillis() - (index * 60000L),
                readTimeMinutes = 1,
                durationSeconds = 0,
                likesCount = 0,
                commentsCount = 0,
                sharesCount = 0,
                isLiked = false,
                isBookmarked = false,
                isSavedOffline = false,
                isUserCreated = true,
                tags = "playground",
                stationFrequency = "",
                issueEdition = "playground"
            )
        }
}
