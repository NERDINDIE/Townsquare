package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.audio.NarrationStyle
import com.example.audio.VoiceNarrationState
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.util.ShareHelper

@Composable
fun ArticleReaderDialog(
    item: MediaItemEntity,
    narrationState: VoiceNarrationState? = null,
    onStartNarration: ((MediaItemEntity, NarrationStyle) -> Unit)? = null,
    onPauseResumeNarration: (() -> Unit)? = null,
    onStopNarration: (() -> Unit)? = null,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onOpenFlipbook: ((MediaItemEntity) -> Unit)? = null,
    onFactCheckClaims: ((MediaItemEntity) -> Unit)? = null,
    onSendLetterToEditor: ((recipientId: String, recipientName: String) -> Unit)? = null,
    showAiFactChecking: Boolean = true,
    showAiVoiceNarration: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var fontScaleStep by remember { mutableIntStateOf(1) } // 0: 15sp, 1: 17sp, 2: 20sp
    var selectedNarrationStyle by remember {
        mutableStateOf(
            if (item.tags.contains("breaking", ignoreCase = true)) NarrationStyle.BREAKING_NEWS
            else if (item.type == MediaType.NEWSLETTER.name) NarrationStyle.PODCAST
            else NarrationStyle.LITERARY_MAGAZINE
        )
    }

    val isNarratingThis = narrationState?.isNarrating == true && narrationState.currentItemTitle == item.title
    val isPausedThis = narrationState?.isPaused == true

    val bodyFontSize = when (fontScaleStep) {
        0 -> 15.sp
        1 -> 17.sp
        else -> 20.sp
    }
    val lineHeight = when (fontScaleStep) {
        0 -> 24.sp
        1 -> 28.sp
        else -> 32.sp
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("article_reader_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top control bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("reader_close_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onBackground)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Font size cycle
                        IconButton(
                            onClick = { fontScaleStep = (fontScaleStep + 1) % 3 },
                            modifier = Modifier.testTag("reader_font_size_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Adjust Font Size",
                                tint = NeonCyan
                            )
                        }

                        // Offline Save
                        IconButton(
                            onClick = { onToggleSavedOffline?.invoke(item) },
                            modifier = Modifier.testTag("reader_offline_button")
                        ) {
                            Icon(
                                imageVector = if (item.isSavedOffline || item.isBookmarked) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                                contentDescription = if (item.isSavedOffline || item.isBookmarked) "Saved Offline" else "Save for Offline",
                                tint = if (item.isSavedOffline || item.isBookmarked) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Bookmark
                        IconButton(
                            onClick = { onToggleBookmark(item) },
                            modifier = Modifier.testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (item.isBookmarked) WarmAmber else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Like
                        IconButton(
                            onClick = { onToggleLike(item) },
                            modifier = Modifier.testTag("reader_like_button")
                        ) {
                            Icon(
                                imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (item.isLiked) CoralRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Fact Check Audit
                        if (showAiFactChecking) {
                            IconButton(
                                onClick = { onFactCheckClaims?.invoke(item) },
                                modifier = Modifier.testTag("reader_fact_check_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FactCheck,
                                    contentDescription = "Fact Check Claims",
                                    tint = NeonCyan
                                )
                            }
                        }

                        // Letter to Editor
                        IconButton(
                            onClick = { onSendLetterToEditor?.invoke("ch_${item.channelName.lowercase()}", "${item.channelName} Desk") },
                            modifier = Modifier.testTag("reader_letter_editor_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = "Send Letter to Editor",
                                tint = WarmAmber
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                if (onShare != null) {
                                    onShare(item)
                                } else {
                                    ShareHelper.shareMediaItem(context, item)
                                }
                            },
                            modifier = Modifier.testTag("reader_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (onOpenFlipbook != null && (item.type == MediaType.NEWSPAPER_MAGAZINE.name || item.type == MediaType.NEWSLETTER.name)) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenFlipbook(item)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF003847),
                                    contentColor = NeonCyan
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("reader_open_flipbook_button")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Flipbook", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                // Voice Narration strip
                if (showAiVoiceNarration) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isNarratingThis) Color(0xFF00222B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, if (isNarratingThis) NeonCyan.copy(alpha = 0.6f) else DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isNarratingThis) NeonCyan else NeonCyan.copy(alpha = 0.2f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isNarratingThis) Icons.Default.GraphicEq else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            tint = if (isNarratingThis) Color(0xFF07090E) else NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isNarratingThis) "Voice Narration Active" else "Townsquare Voice Narration",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isNarratingThis) NeonCyan else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isNarratingThis) {
                                            "Paragraph ${(narrationState?.currentParagraphIndex ?: 0) + 1} of ${narrationState?.totalParagraphs ?: 1} • ${narrationState?.style?.title ?: selectedNarrationStyle.title}"
                                        } else {
                                            "Listen like a podcast or breaking news broadcast"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isNarratingThis) {
                                    IconButton(
                                        onClick = { onPauseResumeNarration?.invoke() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPausedThis) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = if (isPausedThis) "Resume" else "Pause",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onStopNarration?.invoke() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop",
                                            tint = CoralRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { onStartNarration?.invoke(item, selectedNarrationStyle) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonCyan,
                                            contentColor = Color(0xFF07090E)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Voice Aloud", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }

                        // Voice Style Selector Pills
                        if (!isNarratingThis) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NarrationStyle.entries.forEach { style ->
                                    val isSelected = selectedNarrationStyle == style
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedNarrationStyle = style }
                                    ) {
                                        Text(
                                            text = "${style.emoji} ${style.title}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

                // Scrollable Article Reading Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 22.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Category & Publication Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WarmAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (item.type == MediaType.NEWSLETTER.name) "NEWSLETTER" else "LONG-FORM PRESS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WarmAmber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = item.channelName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NeonCyan
                            )

                            if (item.readTimeMinutes > 0) {
                                Text(
                                    text = "• ${item.readTimeMinutes} min read",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Headline
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            lineHeight = 34.sp
                        )

                        if (item.subtitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.subtitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                lineHeight = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Author byline card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = item.authorName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.authorName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = if (item.issueEdition.isNotBlank()) item.issueEdition else item.authorHandle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Featured Cover Banner
                        val coverRes = if (item.type == MediaType.NEWSLETTER.name) {
                            R.drawable.img_morning_brief
                        } else {
                            R.drawable.img_magazine_cover
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                        ) {
                            Image(
                                painter = painterResource(id = coverRes),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Body paragraphs
                        val paragraphs = item.bodyText.split("\n\n")
                        paragraphs.forEach { paragraph ->
                            Text(
                                text = paragraph.trim(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = bodyFontSize,
                                    lineHeight = lineHeight,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.92f),
                                modifier = Modifier.padding(bottom = 18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tags
                        if (item.tags.isNotBlank()) {
                            Text(
                                text = item.tags,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NeonCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(color = DarkBorder, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bottom feedback bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = CoralRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${item.likesCount} readers appreciated this issue",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeonCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Completed",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}
