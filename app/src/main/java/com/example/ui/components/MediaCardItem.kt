package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantPurple
import com.example.ui.theme.WarmAmber
import com.example.util.ShareHelper

@Composable
fun MediaCardItem(
    item: MediaItemEntity,
    audioState: AudioState,
    onOpenReader: (MediaItemEntity) -> Unit,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onVoiceNarrate: ((MediaItemEntity) -> Unit)? = null,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onReportContent: ((MediaItemEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isPlayingThis = audioState.currentItem?.id == item.id && audioState.isPlaying

    when (item.type) {
        MediaType.SOCIAL_POST.name -> {
            SocialPostCard(
                item = item,
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
        MediaType.NEWSPAPER_MAGAZINE.name -> {
            MagazineArticleCard(
                item = item,
                onOpenReader = { onOpenReader(item) },
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
        MediaType.NEWSLETTER.name -> {
            NewsletterCard(
                item = item,
                onOpenReader = { onOpenReader(item) },
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
        MediaType.RADIO_STATION.name -> {
            RadioStationCard(
                item = item,
                isPlaying = isPlayingThis,
                audioState = audioState,
                onPlayToggle = { onPlayAudio(item) },
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
        MediaType.PODCAST_EPISODE.name -> {
            PodcastEpisodeCard(
                item = item,
                isPlaying = isPlayingThis,
                audioState = audioState,
                onPlayToggle = { onPlayAudio(item) },
                onOpenReader = { onOpenReader(item) },
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
        else -> {
            SocialPostCard(
                item = item,
                onToggleLike = { onToggleLike(item) },
                onToggleBookmark = { onToggleBookmark(item) },
                onShare = { onShare?.invoke(item) },
                onVoiceNarrate = onVoiceNarrate?.let { { it(item) } },
                onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },
                onReportContent = onReportContent?.let { { it(item) } },
                modifier = modifier
            )
        }
    }
}

// 1. Social Post Card
@Composable
fun SocialPostCard(
    item: MediaItemEntity,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    onVoiceNarrate: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("social_post_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Author & Channel & Space
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.authorName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (item.isUserCreated) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NeonCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "YOU",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = item.authorHandle.ifBlank { "@creator" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Channel pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = item.channelName,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!item.spaceTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "in Media Space: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.spaceTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = WarmAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body
            Text(
                text = item.bodyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            if (item.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.tags,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action bar
            MediaActionBar(
                likesCount = item.likesCount,
                commentsCount = item.commentsCount,
                sharesCount = item.sharesCount,
                isLiked = item.isLiked,
                isBookmarked = item.isBookmarked,
                isSavedOffline = item.isSavedOffline,
                onToggleLike = onToggleLike,
                onToggleBookmark = onToggleBookmark,
                onVoiceNarrate = onVoiceNarrate,
                onShare = onShare,
                onToggleSavedOffline = onToggleSavedOffline,
                onReportContent = onReportContent
            )
        }
    }
}

// 2. Newspaper / Magazine Article Card
@Composable
fun MagazineArticleCard(
    item: MediaItemEntity,
    onOpenReader: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    onVoiceNarrate: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenReader() }
            .testTag("magazine_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column {
            // Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_magazine_cover),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC0A0E17))
                            )
                        )
                )

                // Header badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarmAmber
                    ) {
                        Text(
                            text = "📰 MAGAZINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2C1600),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "${item.readTimeMinutes} min read",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                // Edition tag at bottom of image
                if (item.issueEdition.isNotBlank()) {
                    Text(
                        text = item.issueEdition,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.channelName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.authorName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onOpenReader,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Read Article", style = MaterialTheme.typography.labelMedium)
                    }

                    MediaActionBar(
                        likesCount = item.likesCount,
                        commentsCount = item.commentsCount,
                        sharesCount = item.sharesCount,
                        isLiked = item.isLiked,
                        isBookmarked = item.isBookmarked,
                        isSavedOffline = item.isSavedOffline,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark,
                        onVoiceNarrate = onVoiceNarrate,
                        onShare = onShare,
                        onToggleSavedOffline = onToggleSavedOffline,
                onReportContent = onReportContent
                    )
                }
            }
        }
    }
}

// 3. Newsletter Card
@Composable
fun NewsletterCard(
    item: MediaItemEntity,
    onOpenReader: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    onVoiceNarrate: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenReader() }
            .testTag("newsletter_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MintTeal.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MintTeal.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MailOutline,
                                contentDescription = null,
                                tint = MintTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "✉️ NEWSLETTER PUBLICATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MintTeal
                        )
                        Text(
                            text = item.issueEdition.ifBlank { "Weekly Digest" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${item.readTimeMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "By ${item.authorName}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.channelName,
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MediaActionBar(
                likesCount = item.likesCount,
                commentsCount = item.commentsCount,
                sharesCount = item.sharesCount,
                isLiked = item.isLiked,
                isBookmarked = item.isBookmarked,
                isSavedOffline = item.isSavedOffline,
                onToggleLike = onToggleLike,
                onToggleBookmark = onToggleBookmark,
                onVoiceNarrate = onVoiceNarrate,
                onShare = onShare,
                onToggleSavedOffline = onToggleSavedOffline,
                onReportContent = onReportContent
            )
        }
    }
}

// 4. Radio Station Card
@Composable
fun RadioStationCard(
    item: MediaItemEntity,
    isPlaying: Boolean,
    audioState: AudioState,
    onPlayToggle: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    onVoiceNarrate: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("radio_station_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) CoralRed.copy(alpha = 0.6f) else RadiantPurple.copy(alpha = 0.35f)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_radio_live),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x33000000), Color(0xDD0A0E17))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CoralRed
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE ON AIR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = item.stationFrequency.ifBlank { "FM 98.5" },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                if (isPlaying) {
                    // Animated mini wave
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        audioState.waveformHeights.take(5).forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height((h * 24).dp.coerceAtLeast(4.dp))
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(NeonCyan)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.subtitle.ifBlank { item.bodyText },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onPlayToggle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlaying) CoralRed else NeonCyan,
                            contentColor = Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("radio_play_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                MediaActionBar(
                    likesCount = item.likesCount,
                    commentsCount = item.commentsCount,
                    sharesCount = item.sharesCount,
                    isLiked = item.isLiked,
                    isBookmarked = item.isBookmarked,
                    isSavedOffline = item.isSavedOffline,
                    onToggleLike = onToggleLike,
                    onToggleBookmark = onToggleBookmark,
                    onVoiceNarrate = onVoiceNarrate,
                    onShare = onShare,
                    onToggleSavedOffline = onToggleSavedOffline,
                onReportContent = onReportContent
                )
            }
        }
    }
}

// 5. Podcast Episode Card
@Composable
fun PodcastEpisodeCard(
    item: MediaItemEntity,
    isPlaying: Boolean,
    audioState: AudioState,
    onPlayToggle: () -> Unit,
    onOpenReader: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    onVoiceNarrate: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("podcast_card_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Cover Art
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_podcast_cover),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay play indicator
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RadiantPurple.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "🎙️ PODCAST",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RadiantPurple,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${(item.durationSeconds / 60).coerceAtLeast(3)} min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Hosted by ${item.authorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.bodyText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPlayToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) CoralRed else NeonCyan,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp).testTag("podcast_play_button_${item.id}")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Playing" else "Listen Episode",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                MediaActionBar(
                    likesCount = item.likesCount,
                    commentsCount = item.commentsCount,
                    sharesCount = item.sharesCount,
                    isLiked = item.isLiked,
                    isBookmarked = item.isBookmarked,
                    isSavedOffline = item.isSavedOffline,
                    onToggleLike = onToggleLike,
                    onToggleBookmark = onToggleBookmark,
                    onVoiceNarrate = onVoiceNarrate,
                    onShare = onShare,
                    onToggleSavedOffline = onToggleSavedOffline,
                onReportContent = onReportContent
                )
            }
        }
    }
}

// Universal action bar for likes, bookmarks, voice narration, sharing, offline
@Composable
fun MediaActionBar(
    likesCount: Int,
    commentsCount: Int,
    sharesCount: Int,
    isLiked: Boolean,
    isBookmarked: Boolean,
    isSavedOffline: Boolean = false,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onVoiceNarrate: (() -> Unit)? = null,
    onShare: (() -> Unit)? = null,
    onToggleSavedOffline: (() -> Unit)? = null,
    onReportContent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val heartColor by animateColorAsState(if (isLiked) CoralRed else MaterialTheme.colorScheme.onSurfaceVariant)
    val bookmarkColor by animateColorAsState(if (isBookmarked) WarmAmber else MaterialTheme.colorScheme.onSurfaceVariant)
    val offlineColor by animateColorAsState(if (isSavedOffline || isBookmarked) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onToggleLike() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = heartColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likesCount.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = heartColor
                )
            }

            // Bookmark (Saved in Profile)
            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = bookmarkColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Voice Narrate Button
            if (onVoiceNarrate != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonCyan.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onVoiceNarrate() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Voice Narrate",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Narrate",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Right side: Share & Offline Save
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Offline Save Toggle
            if (onToggleSavedOffline != null) {
                IconButton(
                    onClick = onToggleSavedOffline,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isSavedOffline || isBookmarked) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                        contentDescription = if (isSavedOffline || isBookmarked) "Saved Offline" else "Save for Offline",
                        tint = offlineColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Share
            if (onShare != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onShare() }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    if (sharesCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sharesCount.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Report
            if (onReportContent != null) {
                IconButton(
                    onClick = onReportContent,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Report",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
