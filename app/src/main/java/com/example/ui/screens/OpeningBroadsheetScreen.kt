package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

enum class SplashStage {
    WORDMARK,
    BROADSHEET,
    ENTERED
}

/**
 * Combined entry splash flow containing the wordmark animation and
 * the broadsheet/magazine style front page.
 */
@Composable
fun OpeningBroadsheetFlow(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit
) {
    var stage by remember { mutableStateOf(SplashStage.WORDMARK) }

    when (stage) {
        SplashStage.WORDMARK -> {
            WordmarkSplashScreen(
                onFinished = { stage = SplashStage.BROADSHEET }
            )
        }
        SplashStage.BROADSHEET -> {
            BroadsheetOpeningScreen(
                feedItems = feedItems,
                journalEditions = journalEditions,
                bulletins = bulletins,
                onEnterHomepage = {
                    stage = SplashStage.ENTERED
                    onEnterHomepage()
                }
            )
        }
        SplashStage.ENTERED -> {
            // Already entered, trigger callback
            LaunchedEffect(Unit) {
                onEnterHomepage()
            }
        }
    }
}

/**
 * Animated Wordmark Splash Screen that plays each time the app is opened.
 */
@Composable
fun WordmarkSplashScreen(
    onFinished: () -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }

    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.88f) }
    val lineWidthAnim = remember { Animatable(0f) }
    val subtitleAlphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationStarted = true
        alphaAnim.animateTo(1f, animationSpec = tween(700, easing = LinearOutSlowInEasing))
        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = 0.65f, stiffness = 120f))
        lineWidthAnim.animateTo(240f, animationSpec = tween(900, easing = FastOutSlowInEasing))
        subtitleAlphaAnim.animateTo(1f, animationSpec = tween(600))
        
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070B12),
                        Color(0xFF0F172A),
                        Color(0xFF080C14)
                    )
                )
            )
            .clickable { onFinished() }
            .testTag("wordmark_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background mesh circles
        Box(
            modifier = Modifier
                .size(320.dp)
                .alpha(0.06f)
                .background(WarmAmber, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            // Ornate Masthead Crest Icon
            Surface(
                shape = CircleShape,
                color = WarmAmber.copy(alpha = 0.15f),
                border = BorderStroke(1.5.dp, WarmAmber.copy(alpha = 0.4f)),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "Townsquare Press Crest",
                        tint = WarmAmber,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edition Tag Header
            Text(
                text = "EST. 2026 • DIGITAL PRESS WIRE",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = WarmAmber
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main Animated Wordmark
            Text(
                text = "TOWNSQUARE",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expanding Masthead Divider Line
            Box(
                modifier = Modifier
                    .width(lineWidthAnim.value.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                WarmAmber,
                                NeonCyan,
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle
            Text(
                text = "THE DAILY CHRONICLE & MEDIA DIGEST",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlphaAnim.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "VOL. CVII • MORNING BROADCAST",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                color = Color.Gray,
                modifier = Modifier.alpha(subtitleAlphaAnim.value)
            )
        }

        // Bottom Tap to Skip Hint & Animated Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(140.dp)
                    .height(3.dp)
                    .clip(CircleShape),
                color = WarmAmber,
                trackColor = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Opening today's edition • Tap to skip",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

/**
 * Broadsheet / Magazine Style Front-Page Screen populated dynamically with real app content.
 */
@Composable
fun BroadsheetOpeningScreen(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US) }
    val currentDateStr = remember { dateFormat.format(Date()).uppercase(Locale.US) }

    val leadItem = feedItems.firstOrNull() ?: MediaItemEntity(
        id = 1,
        type = "NEWSPAPER_MAGAZINE",
        title = "Civic Renewal & Modern Press Wire Takes Center Stage",
        subtitle = "A comprehensive look into digital dispatches and local community journalism",
        authorName = "Editorial Desk",
        channelId = "townsquare",
        channelName = "Townsquare Gazette",
        bodyText = "Welcome to today's edition of the Townsquare Gazette. Featuring real-time dispatches, audio broadcasts, visual gallery collections, and civic news directly from local creators across the municipality.",
        readTimeMinutes = 4
    )

    val secondaryItems = feedItems.drop(1).take(2)
    val featuredJournal = journalEditions.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("broadsheet_opening_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // 1. TOP BROADSHEET MASTHEAD BANNER
            Surface(
                color = Color(0xFF0B0F17),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Date & Edition Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentDateStr,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = WarmAmber
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WarmAmber.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, WarmAmber.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "MORNING EDITION • VOL. CVII",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = WarmAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "72°F SUNNY",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Ornate Masthead Title
                    Text(
                        text = "THE TOWNSQUARE CHRONICLE",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "“ALL THE DISPATCHES, JOURNALS, AUDIO & CIVIC NEWS FIT TO PRINT”",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF334155), thickness = 2.dp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. LEAD STORY FEATURE (FRONT PAGE COVER ARTICLE)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WarmAmber,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "🔥 LEAD DISPATCH • ${leadItem.channelName.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(
                            text = "${leadItem.readTimeMinutes} MIN READ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = leadItem.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "By ${leadItem.authorName} • Townsquare Editorial Staff",
                        style = MaterialTheme.typography.labelMedium,
                        color = WarmAmber
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lead image preview if present
                    if (leadItem.mediaUrl.isNotBlank()) {
                        AsyncImage(
                            model = leadItem.mediaUrl,
                            contentDescription = leadItem.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        // Styled Editorial Frame Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Newspaper,
                                    contentDescription = null,
                                    tint = WarmAmber,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "SPECIAL FRONT PAGE REPORT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = WarmAmber
                                    )
                                    Text(
                                        text = if (leadItem.subtitle.isNotBlank()) leadItem.subtitle else "Latest breaking civic news and multimedia updates.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Body Excerpt with Drop-Cap design
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val firstChar = leadItem.bodyText.firstOrNull()?.toString() ?: "W"
                        val remainingText = leadItem.bodyText.drop(1).take(220) + "..."

                        Text(
                            text = firstChar,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Black
                            ),
                            color = WarmAmber,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = remainingText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                lineHeight = 20.sp
                            ),
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. SECONDARY COLUMNS DISPATCHES (NEWSGRID)
            if (secondaryItems.isNotEmpty()) {
                Text(
                    text = "TODAY'S FRONT-PAGE DISPATCHES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = WarmAmber,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    secondaryItems.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = item.channelName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = NeonCyan
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.bodyText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFF94A3B8),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. EDITOR'S DESK NOTE / FEATURED JOURNAL
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141D2B)),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "EDITOR'S NOTE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (!featuredJournal?.editorialNotes.isNullOrBlank()) "“${featuredJournal?.editorialNotes}”" else "“Welcome to Townsquare. Swipe through live radio, local journals, breaking press wires, and community video streams.”",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. FLOATING BOTTOM ACTION BAR TO ENTER HOMEPAGE
        Surface(
            color = Color(0xFF0B0F17).copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TOWNSQUARE EDITION READY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Text(
                        text = "Tap to enter the full newsroom & media app",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                Button(
                    onClick = { onEnterHomepage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmAmber,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("enter_homepage_button")
                ) {
                    Text(
                        text = "ENTER HOMEPAGE",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Enter Homepage",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
