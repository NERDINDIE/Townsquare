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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.ui.plus.extensions.welcome.*
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
    onEnterHomepage: () -> Unit,
    activeSkinId: String = "BROADSHEET"
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
                activeSkinId = activeSkinId,
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
            .statusBarsPadding()
            .navigationBarsPadding()
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

enum class WelcomeSkinType(val label: String, val emoji: String) {
    BROADSHEET("Broadsheet Press", "📰"),
    TABLOID("Red Tabloid", "🔥"),
    MAGAZINE("Glossy Magazine", "🖼️"),
    DASHBOARD("Ops Dashboard", "📊"),
    KEITAI("Keitai i-Mode", "📲"),
    MANUSCRIPT("Gutenberg Codex", "📜"),
    Y2K_DESKTOP("Y2K Desktop", "💻")
}

/**
 * Front-Page Screen supporting modular Welcome Extension Skins.
 * The active skin is configured via Settings and applied automatically.
 */
@Composable
fun BroadsheetOpeningScreen(
    feedItems: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    bulletins: List<LocalBulletinEntity>,
    onEnterHomepage: () -> Unit,
    activeSkinId: String = "BROADSHEET"
) {
    val selectedSkin = remember(activeSkinId) {
        WelcomeSkinType.entries.find { it.name.equals(activeSkinId, ignoreCase = true) }
            ?: WelcomeSkinType.BROADSHEET
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B12))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("broadsheet_opening_screen")
    ) {
        // RENDER SELECTED WELCOME EXTENSION SKIN
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedSkin) {
                WelcomeSkinType.BROADSHEET -> BroadsheetWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.TABLOID -> TabloidWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.MAGAZINE -> MagazineWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.DASHBOARD -> DashboardWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.KEITAI -> KeitaiWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.MANUSCRIPT -> ManuscriptWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
                WelcomeSkinType.Y2K_DESKTOP -> Y2KDesktopWelcomeSkin(feedItems, journalEditions, bulletins, onEnterHomepage)
            }
        }
    }
}

