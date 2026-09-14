package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.NarrationStyle
import com.example.audio.VoiceNarrationState
import com.example.data.model.FlipbookPageData
import com.example.data.model.JournalEditionEntity
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.util.ShareHelper
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class PaperTint(
    val label: String,
    val paperColor: Color,
    val inkColor: Color,
    val subtitleColor: Color,
    val dividerColor: Color
) {
    SEPIA(
        label = "Sepia Newsprint",
        paperColor = Color(0xFFF6F1E5),
        inkColor = Color(0xFF1D1B18),
        subtitleColor = Color(0xFF5A544A),
        dividerColor = Color(0xFFDCD2C0)
    ),
    CREAM(
        label = "Parchment Broadsheet",
        paperColor = Color(0xFFFAF8F2),
        inkColor = Color(0xFF181C24),
        subtitleColor = Color(0xFF4B5363),
        dividerColor = Color(0xFFE2DDD3)
    ),
    MIDNIGHT(
        label = "Midnight Ink",
        paperColor = Color(0xFF17191E),
        inkColor = Color(0xFFEEEAE2),
        subtitleColor = Color(0xFFAAA49B),
        dividerColor = Color(0xFF2C2F36)
    )
}

/**
 * Full-featured physical e-flipbook reader dialog with haptic feedback,
 * realistic 3D curl perspective, spine drop-shadow, broadsheet page layout,
 * and multi-page reading controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipbookReaderDialog(
    mediaItem: MediaItemEntity? = null,
    journalEdition: JournalEditionEntity? = null,
    narrationState: VoiceNarrationState? = null,
    onStartNarration: ((title: String, text: String, author: String, style: NarrationStyle) -> Unit)? = null,
    onStopNarration: (() -> Unit)? = null,
    onShare: ((String, String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (mediaItem == null && journalEdition == null) return

    val context = LocalContext.current
    val pages = remember(mediaItem, journalEdition) {
        buildFlipbookPages(mediaItem, journalEdition)
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    var paperTint by remember { mutableStateOf(PaperTint.SEPIA) }
    var isTwoColumn by remember { mutableStateOf(true) }
    var isLargeText by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var isAudioNarrating by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Interactive flip drag animation state
    val dragOffsetFraction = remember { Animatable(0f) }
    val density = LocalDensity.current

    // Observe active narration
    val isRealNarrating = narrationState?.isNarrating == true

    LaunchedEffect(currentPageIndex, isAudioNarrating) {
        if (isAudioNarrating && onStartNarration != null && currentPageIndex in pages.indices) {
            val p = pages[currentPageIndex]
            val pageText = p.paragraphs.joinToString("\n\n")
            onStartNarration(p.headline, pageText, p.author, NarrationStyle.LITERARY_MAGAZINE)
        }
    }

    fun flipToPage(targetPage: Int) {
        if (targetPage in pages.indices && targetPage != currentPageIndex) {
            val forward = targetPage > currentPageIndex
            coroutineScope.launch {
                // Animate flip curl
                dragOffsetFraction.animateTo(
                    targetValue = if (forward) -1f else 1f,
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                )
                currentPageIndex = targetPage
                // Paper drop haptic snap
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                dragOffsetFraction.snapTo(0f)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("flipbook_reader_dialog"),
            color = Color(0xFF0F1115)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWidthPx = with(density) { maxWidth.toPx() }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Navigation Bar
                    AnimatedVisibility(visible = showControls, enter = fadeIn(), exit = fadeOut()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF14171E))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.testTag("flipbook_close_button")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close Flipbook", tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = pages[currentPageIndex].mastheadTitle,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${pages[currentPageIndex].issueEdition} • Page ${currentPageIndex + 1} of ${pages.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonCyan
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Column layout toggle
                                IconButton(
                                    onClick = {
                                        isTwoColumn = !isTwoColumn
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.testTag("toggle_columns_button")
                                ) {
                                    Icon(
                                        imageVector = if (isTwoColumn) Icons.Default.ViewAgenda else Icons.Default.ViewColumn,
                                        contentDescription = "Toggle Column Layout",
                                        tint = if (isTwoColumn) NeonCyan else Color.LightGray
                                    )
                                }

                                // Large text toggle
                                IconButton(
                                    onClick = {
                                        isLargeText = !isLargeText
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.testTag("toggle_font_size_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = "Adjust Font Size",
                                        tint = if (isLargeText) WarmAmber else Color.LightGray
                                    )
                                }

                                // Paper Tint cycler
                                IconButton(
                                    onClick = {
                                        paperTint = when (paperTint) {
                                            PaperTint.SEPIA -> PaperTint.CREAM
                                            PaperTint.CREAM -> PaperTint.MIDNIGHT
                                            PaperTint.MIDNIGHT -> PaperTint.SEPIA
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.testTag("cycle_paper_tint_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Paper Tint",
                                        tint = when (paperTint) {
                                            PaperTint.SEPIA -> Color(0xFFD4A373)
                                            PaperTint.CREAM -> Color(0xFFFAF8F2)
                                            PaperTint.MIDNIGHT -> Color(0xFF6C757D)
                                        }
                                    )
                                }

                                // Share Page
                                IconButton(
                                    onClick = {
                                        val p = pages[currentPageIndex]
                                        val pageText = p.paragraphs.joinToString("\n\n")
                                        if (onShare != null) {
                                            onShare(p.headline, "${p.headline}\n\n$pageText")
                                        } else {
                                            ShareHelper.shareText(context, p.headline, "${p.headline}\n\n$pageText\n\nVia Townsquare Civic Media")
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    modifier = Modifier.testTag("flipbook_share_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Page",
                                        tint = Color.LightGray
                                    )
                                }

                                // Voice Narration
                                IconButton(
                                    onClick = {
                                        val nextState = !isAudioNarrating
                                        isAudioNarrating = nextState
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (!nextState) {
                                            onStopNarration?.invoke()
                                        } else if (onStartNarration != null && currentPageIndex in pages.indices) {
                                            val p = pages[currentPageIndex]
                                            val pageText = p.paragraphs.joinToString("\n\n")
                                            onStartNarration(p.headline, pageText, p.author, NarrationStyle.LITERARY_MAGAZINE)
                                        }
                                    },
                                    modifier = Modifier.testTag("toggle_audio_narration_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Audio Reading",
                                        tint = if (isAudioNarrating || isRealNarrating) WarmAmber else Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    // Audio Narration Banner
                    if (isAudioNarrating || isRealNarrating) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF231D12))
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎙️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Townsquare Audio Dispatch • Voice Aloud",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WarmAmber
                                )
                                Text(
                                    "Reading aloud: ${pages[currentPageIndex].headline}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    isAudioNarrating = false
                                    onStopNarration?.invoke()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Stop", tint = WarmAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // MAIN PHYSICAL FLIPBOOK SPREAD
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF101216))
                            .pointerInput(currentPageIndex) {
                                var accumulatedDrag = 0f
                                var thresholdTriggered = false
                                detectDragGestures(
                                    onDragStart = {
                                        accumulatedDrag = 0f
                                        thresholdTriggered = false
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        accumulatedDrag += dragAmount.x
                                        val fraction = (accumulatedDrag / boxWidthPx).coerceIn(-1f, 1f)
                                        coroutineScope.launch {
                                            dragOffsetFraction.snapTo(fraction)
                                        }
                                        if (!thresholdTriggered && abs(fraction) > 0.25f) {
                                            // Haptic resistance feeling
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            thresholdTriggered = true
                                        }
                                    },
                                    onDragEnd = {
                                        val currentFraction = dragOffsetFraction.value
                                        if (currentFraction < -0.20f && currentPageIndex < pages.size - 1) {
                                            // Complete turn forward
                                            coroutineScope.launch {
                                                dragOffsetFraction.animateTo(-1f, tween(180, easing = FastOutSlowInEasing))
                                                currentPageIndex++
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                dragOffsetFraction.snapTo(0f)
                                            }
                                        } else if (currentFraction > 0.20f && currentPageIndex > 0) {
                                            // Complete turn backward
                                            coroutineScope.launch {
                                                dragOffsetFraction.animateTo(1f, tween(180, easing = FastOutSlowInEasing))
                                                currentPageIndex--
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                dragOffsetFraction.snapTo(0f)
                                            }
                                        } else {
                                            // Spring back to rest
                                            coroutineScope.launch {
                                                dragOffsetFraction.animateTo(0f, tween(160, easing = FastOutSlowInEasing))
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch { dragOffsetFraction.animateTo(0f) }
                                    }
                                )
                            }
                    ) {
                        val dragProgress = dragOffsetFraction.value

                        // UNDERNEATH PAGE (Visible as upper page curls away)
                        val underneathPageIndex = if (dragProgress < 0 && currentPageIndex < pages.size - 1) {
                            currentPageIndex + 1
                        } else if (dragProgress > 0 && currentPageIndex > 0) {
                            currentPageIndex - 1
                        } else null

                        if (underneathPageIndex != null) {
                            PhysicalBroadsheetPaper(
                                page = pages[underneathPageIndex],
                                paperTint = paperTint,
                                isTwoColumn = isTwoColumn,
                                isLargeText = isLargeText,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }

                        // CURRENT ACTIVE PAGE (Rotates with 3D curl physics)
                        val rotationY = (dragProgress * 95f).coerceIn(-95f, 95f)
                        val isTurningForward = dragProgress < 0
                        val spineTransformOrigin = if (isTurningForward) TransformOrigin(0f, 0.5f) else TransformOrigin(1f, 0.5f)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    cameraDistance = 16f * density.density
                                    this.transformOrigin = spineTransformOrigin
                                    this.rotationY = rotationY
                                    shadowElevation = (abs(dragProgress) * 24.dp.toPx())
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            PhysicalBroadsheetPaper(
                                page = pages[currentPageIndex],
                                paperTint = paperTint,
                                isTwoColumn = isTwoColumn,
                                isLargeText = isLargeText,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Dynamic curl fold shadow overlay
                            if (abs(dragProgress) > 0.01f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = if (isTurningForward) listOf(
                                                    Color.Black.copy(alpha = (abs(dragProgress) * 0.45f).coerceAtMost(0.6f)),
                                                    Color.Transparent
                                                ) else listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = (abs(dragProgress) * 0.45f).coerceAtMost(0.6f))
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        // Physical Spine Book Crease (Center shadow)
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(18.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Left and Right tap zones for instant flipping
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .fillMaxHeight()
                                .width(48.dp)
                                .clickable {
                                    if (currentPageIndex > 0) flipToPage(currentPageIndex - 1)
                                }
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight()
                                .width(48.dp)
                                .clickable {
                                    if (currentPageIndex < pages.size - 1) flipToPage(currentPageIndex + 1)
                                }
                        )
                    }

                    // BOTTOM CONTROL DOCK & SCRUBBER
                    AnimatedVisibility(visible = showControls, enter = fadeIn(), exit = fadeOut()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF14171E))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Scrubber row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = {
                                        if (currentPageIndex > 0) flipToPage(currentPageIndex - 1)
                                    },
                                    enabled = currentPageIndex > 0,
                                    modifier = Modifier.testTag("flipbook_prev_page_button")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Page",
                                        tint = if (currentPageIndex > 0) Color.White else Color.DarkGray
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${pages[currentPageIndex].sectionHeader} • ${pages[currentPageIndex].dateString}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = WarmAmber
                                    )
                                    Slider(
                                        value = currentPageIndex.toFloat(),
                                        onValueChange = { targetFloat ->
                                            val targetInt = targetFloat.toInt().coerceIn(pages.indices)
                                            if (targetInt != currentPageIndex) {
                                                currentPageIndex = targetInt
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                        },
                                        valueRange = 0f..(pages.size - 1).toFloat(),
                                        steps = (pages.size - 2).coerceAtLeast(0),
                                        colors = SliderDefaults.colors(
                                            thumbColor = NeonCyan,
                                            activeTrackColor = NeonCyan,
                                            inactiveTrackColor = Color.DarkGray
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .testTag("flipbook_page_slider")
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (currentPageIndex < pages.size - 1) flipToPage(currentPageIndex + 1)
                                    },
                                    enabled = currentPageIndex < pages.size - 1,
                                    modifier = Modifier.testTag("flipbook_next_page_button")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Page",
                                        tint = if (currentPageIndex < pages.size - 1) Color.White else Color.DarkGray
                                    )
                                }
                            }

                            // Quick page thumbnail chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                pages.forEachIndexed { index, page ->
                                    val isSelected = index == currentPageIndex
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) NeonCyan else Color(0xFF222631))
                                            .clickable { flipToPage(index) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "P.${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) Color(0xFF00303D) else Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic Broadsheet/Magazine Physical Paper Page with vintage mastheads,
 * column rules, ornate borders, drop caps, and newsprint aesthetic.
 */
@Composable
fun PhysicalBroadsheetPaper(
    page: FlipbookPageData,
    paperTint: PaperTint,
    isTwoColumn: Boolean,
    isLargeText: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp)),
        colors = CardDefaults.cardColors(containerColor = paperTint.paperColor),
        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 8.dp, bottomEnd = 8.dp),
        border = BorderStroke(1.dp, paperTint.dividerColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // MASTHEAD & BROADSHEET BANNER
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Double rule top line
                    Divider(color = paperTint.inkColor, thickness = 2.dp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Divider(color = paperTint.inkColor, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Section & Date strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = page.sectionHeader.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                            color = paperTint.subtitleColor
                        )
                        Text(
                            text = page.dateString.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp),
                            color = paperTint.subtitleColor
                        )
                        Text(
                            text = page.issueEdition.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = paperTint.subtitleColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = paperTint.dividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Grand Masthead Title
                    Text(
                        text = page.mastheadTitle,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 0.5.sp
                        ),
                        color = paperTint.inkColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Divider(color = paperTint.inkColor, thickness = 1.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // HEADLINE & SUBHEADLINE
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = page.headline,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 28.sp,
                            fontSize = if (isLargeText) 23.sp else 20.sp
                        ),
                        color = paperTint.inkColor
                    )

                    if (page.subheadline.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = page.subheadline,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                lineHeight = 20.sp,
                                fontSize = if (isLargeText) 16.sp else 14.sp
                            ),
                            color = paperTint.subtitleColor
                        )
                    }

                    if (page.author.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "By ${page.author}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = paperTint.inkColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("•", color = paperTint.subtitleColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Townsquare Press Guild",
                                style = MaterialTheme.typography.labelSmall,
                                color = paperTint.subtitleColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = paperTint.dividerColor, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // PULL QUOTE (If available on this page)
            if (page.pullQuote.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = paperTint.paperColor),
                        border = BorderStroke(1.dp, paperTint.inkColor.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "“${page.pullQuote}”",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 24.sp
                                ),
                                color = paperTint.inkColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (page.pullQuoteAuthor.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "— ${page.pullQuoteAuthor}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = paperTint.subtitleColor,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // PHOTO CAPTION / VISUAL SPOTLIGHT
            if (page.photoCaption.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = paperTint.dividerColor.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📰", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ILLUSTRATION & ARCHIVAL SKETCH",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = paperTint.subtitleColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = page.photoCaption,
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = paperTint.inkColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // BULLET POINTS / TOWN BULLETINS
            if (page.bulletPoints.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "TOWN ALMANAC & DISPATCH BULLETINS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = paperTint.inkColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        page.bulletPoints.forEach { point ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "▪",
                                    color = paperTint.inkColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 20.sp,
                                        fontSize = if (isLargeText) 15.sp else 13.5.sp
                                    ),
                                    color = paperTint.inkColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = paperTint.dividerColor, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // BROADSHEET ARTICLE PARAGRAPHS (Columns or Single Flow)
            if (isTwoColumn) {
                // Render as paired columns or alternating broadsheet format
                val half = (page.paragraphs.size + 1) / 2
                val col1 = page.paragraphs.take(half)
                val col2 = page.paragraphs.drop(half)

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            col1.forEachIndexed { idx, para ->
                                BroadsheetParagraph(
                                    text = para,
                                    isDropCap = idx == 0 && page.pageNumber == 1,
                                    paperTint = paperTint,
                                    isLargeText = isLargeText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        // Column separation line
                        Box(
                            modifier = Modifier
                                .width(0.8.dp)
                                .fillMaxHeight()
                                .background(paperTint.dividerColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            col2.forEach { para ->
                                BroadsheetParagraph(
                                    text = para,
                                    isDropCap = false,
                                    paperTint = paperTint,
                                    isLargeText = isLargeText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            } else {
                // Single column flow
                items(page.paragraphs) { para ->
                    BroadsheetParagraph(
                        text = para,
                        isDropCap = para == page.paragraphs.firstOrNull() && page.pageNumber == 1,
                        paperTint = paperTint,
                        isLargeText = isLargeText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // FOOTER IMPRINT
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = paperTint.dividerColor, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Townsquare Electronic Press • Page ${page.pageNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = paperTint.subtitleColor
                    )
                    Text(
                        text = "Turn page for continued edition ➔",
                        style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                        color = paperTint.subtitleColor
                    )
                }
            }
        }
    }
}

/**
 * Broadsheet paragraph with authentic classic drop cap for the lead sentence.
 */
@Composable
fun BroadsheetParagraph(
    text: String,
    isDropCap: Boolean,
    paperTint: PaperTint,
    isLargeText: Boolean
) {
    if (isDropCap && text.length > 2) {
        val firstChar = text.take(1)
        val restOfText = text.drop(1)
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(paperTint.inkColor, shape = RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstChar,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    ),
                    color = paperTint.paperColor
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = restOfText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    lineHeight = if (isLargeText) 23.sp else 20.sp,
                    fontSize = if (isLargeText) 16.sp else 13.5.sp
                ),
                color = paperTint.inkColor
            )
        }
    } else {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Serif,
                lineHeight = if (isLargeText) 23.sp else 20.sp,
                fontSize = if (isLargeText) 16.sp else 13.5.sp
            ),
            color = paperTint.inkColor
        )
    }
}

/**
 * Builds a multi-page physical broadsheet representation for any newspaper,
 * magazine, or user-created journal edition.
 */
fun buildFlipbookPages(
    mediaItem: MediaItemEntity?,
    journalEdition: JournalEditionEntity?
): List<FlipbookPageData> {
    if (journalEdition != null) {
        val paragraphs = journalEdition.leadArticleBody.split("\n\n").filter { it.isNotBlank() }
        val secondaryParas = journalEdition.secondaryArticleBody.split("\n\n").filter { it.isNotBlank() }

        return listOf(
            FlipbookPageData(
                pageNumber = 1,
                totalPages = 4,
                sectionHeader = "Front Page • City Edition",
                mastheadTitle = journalEdition.newspaperTitle,
                issueEdition = "Vol. ${journalEdition.volumeNumber}, Issue ${journalEdition.issueNumber}",
                dateString = journalEdition.issueDate,
                headline = journalEdition.leadHeadline,
                subheadline = journalEdition.leadSubheadline,
                author = journalEdition.leadAuthor,
                paragraphs = if (paragraphs.isNotEmpty()) paragraphs.take(3) else listOf(journalEdition.leadArticleBody),
                pullQuote = journalEdition.motto,
                pullQuoteAuthor = "The Masthead Motto"
            ),
            FlipbookPageData(
                pageNumber = 2,
                totalPages = 4,
                sectionHeader = "Page 2 • Investigative Ledger",
                mastheadTitle = journalEdition.newspaperTitle,
                issueEdition = "Vol. ${journalEdition.volumeNumber}, Issue ${journalEdition.issueNumber}",
                dateString = journalEdition.issueDate,
                headline = journalEdition.secondaryHeadline.ifEmpty { "Continued Coverage & City Analysis" },
                subheadline = "Detailed reporting from the Townsquare press desks.",
                author = journalEdition.leadAuthor,
                paragraphs = if (secondaryParas.isNotEmpty()) secondaryParas else paragraphs.drop(3).ifEmpty {
                    listOf("In-depth analysis and economic commentary continue as civic developments unfold across the regional market quarters.")
                },
                photoCaption = "Community members gather in the civic forum to discuss the newly published agenda."
            ),
            FlipbookPageData(
                pageNumber = 3,
                totalPages = 4,
                sectionHeader = "Page 3 • From the Editor's Desk",
                mastheadTitle = journalEdition.newspaperTitle,
                issueEdition = "Vol. ${journalEdition.volumeNumber}, Issue ${journalEdition.issueNumber}",
                dateString = journalEdition.issueDate,
                headline = "Notes & Reflections on the Public Interest",
                subheadline = "An editorial address to the citizens of Townsquare.",
                author = journalEdition.leadAuthor,
                paragraphs = listOf(
                    journalEdition.editorialNotes.ifEmpty {
                        "As editors and custodians of this printed record, our pledge is to elevate the voices of artisans, neighbors, and civic organizers who strengthen our collective bonds."
                    },
                    "Every issue archived here represents a chapter in the living history of our neighborhood. We invite correspondence, letters, and guest columns for upcoming editions."
                ),
                pullQuote = "Local storytelling is the cornerstone of civic wisdom.",
                pullQuoteAuthor = journalEdition.leadAuthor
            ),
            FlipbookPageData(
                pageNumber = 4,
                totalPages = 4,
                sectionHeader = "Back Page • Town Almanac & Bulletins",
                mastheadTitle = journalEdition.newspaperTitle,
                issueEdition = "Vol. ${journalEdition.volumeNumber}, Issue ${journalEdition.issueNumber}",
                dateString = journalEdition.issueDate,
                headline = "Community Almanac, Weather & Market Classifieds",
                subheadline = "Official public notices and daily regional forecasts.",
                author = "Town Registry",
                paragraphs = listOf(
                    "The Townsquare Press Guild archives all editions in the permanent municipal library for public study and civic research.",
                    "Citizens interested in setting type or composing new editions are welcome at the print shop on Market Street every morning."
                ),
                bulletPoints = if (journalEdition.communityBulletin.isNotEmpty()) {
                    journalEdition.communityBulletin.split("•").map { it.trim() }.filter { it.isNotEmpty() }
                } else {
                    listOf(
                        "Townsquare Farmers Market: Open Thursday - Saturday 8 AM to 2 PM",
                        "Public Observatory Viewing: Friday evening from twilight till midnight",
                        "Local Forecast: Clear skies, High 73°F / Low 52°F, West winds 5 mph"
                    )
                }
            )
        )
    }

    // From MediaItemEntity
    val item = mediaItem ?: return emptyList()
    val paragraphs = item.bodyText.split("\n\n").filter { it.isNotBlank() }

    return listOf(
        FlipbookPageData(
            pageNumber = 1,
            totalPages = 4,
            sectionHeader = "Front Page Broadsheet",
            mastheadTitle = if (item.issueEdition.isNotEmpty()) item.issueEdition.substringBefore("•").trim() else "The Townsquare Chronicle",
            issueEdition = item.issueEdition.ifEmpty { "Morning City Edition" },
            dateString = "Daily Dispatch",
            headline = item.title,
            subheadline = item.subtitle,
            author = item.authorName,
            paragraphs = paragraphs.take(3).ifEmpty { listOf(item.bodyText) },
            pullQuote = "Honest reporting is the cornerstone of a vibrant democracy.",
            pullQuoteAuthor = item.authorName
        ),
        FlipbookPageData(
            pageNumber = 2,
            totalPages = 4,
            sectionHeader = "Page 2 • In-Depth Analysis",
            mastheadTitle = if (item.issueEdition.isNotEmpty()) item.issueEdition.substringBefore("•").trim() else "The Townsquare Chronicle",
            issueEdition = item.issueEdition.ifEmpty { "Morning City Edition" },
            dateString = "Special Feature",
            headline = "The Broader Landscape: Impact & Implications",
            subheadline = "How today's developments reshape policy, commerce, and community life.",
            author = item.authorName,
            paragraphs = paragraphs.drop(3).ifEmpty {
                listOf(
                    "Observers across civic institutions note that public momentum has shifted definitively toward sustainable, localized solutions that respect both heritage and forward-looking engineering.",
                    "Local merchants report that community response has exceeded preliminary forecasts, generating renewed interest in neighborhood cooperatives and shared open-air spaces."
                )
            },
            photoCaption = "Field sketch capturing community members discussing local initiatives in the central market square."
        ),
        FlipbookPageData(
            pageNumber = 3,
            totalPages = 4,
            sectionHeader = "Page 3 • Voices & Commentary",
            mastheadTitle = if (item.issueEdition.isNotEmpty()) item.issueEdition.substringBefore("•").trim() else "The Townsquare Chronicle",
            issueEdition = item.issueEdition.ifEmpty { "Morning City Edition" },
            dateString = "Opinion & Op-Ed",
            headline = "Reflections from the Town Square",
            subheadline = "Perspectives from citizens, artisans, and visiting scholars.",
            author = "Townsquare Forum",
            paragraphs = listOf(
                "When communities take ownership of their media and physical environments, resilience naturally follows. The lessons learned in our district offer a blueprint for towns everywhere.",
                "From our morning coffees to evening strolls, the cadence of daily life feels richer when we are engaged participants rather than passive consumers."
            ),
            pullQuote = "We do not merely report history; we participate in shaping it together.",
            pullQuoteAuthor = "Town Editorial Desk"
        ),
        FlipbookPageData(
            pageNumber = 4,
            totalPages = 4,
            sectionHeader = "Back Page • Civic Almanac",
            mastheadTitle = if (item.issueEdition.isNotEmpty()) item.issueEdition.substringBefore("•").trim() else "The Townsquare Chronicle",
            issueEdition = item.issueEdition.ifEmpty { "Morning City Edition" },
            dateString = "Daily Almanac",
            headline = "Community Exchange, Ticker & Weather",
            subheadline = "Essential notices for residents and visitors.",
            author = "Town Registrar",
            paragraphs = listOf(
                "Printed and published via Townsquare Media System. Archived in local database under permanent civic record."
            ),
            bulletPoints = listOf(
                "Townsquare Microgrid: Operating at 100% capacity with 48 MWh clean reserve",
                "Community Reading Circle: Wednesday at 6 PM in the Municipal Library",
                "Regional Electric Rail: On schedule across all 12 town platforms",
                "Weather Outlook: 72°F Sunny, gentle breeze, zero air pollution index"
            )
        )
    )
}
