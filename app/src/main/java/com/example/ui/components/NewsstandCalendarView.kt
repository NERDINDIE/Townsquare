package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UpcomingEditionEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsstandCalendarView(
    upcomingEditions: List<UpcomingEditionEntity>,
    onToggleReminder: (UpcomingEditionEntity) -> Unit,
    onToggleSubscription: (UpcomingEditionEntity) -> Unit,
    onAnnounceEdition: (
        title: String,
        type: String,
        volume: String,
        releaseDate: String,
        dayLabel: String,
        releaseTime: String,
        headline: String,
        teaser: String,
        notes: String,
        colorHex: Long,
        onSuccess: () -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var selectedFilter by remember { mutableStateOf("FOLLOWED") } // "FOLLOWED", "ALL", "BROADSHEET", "MAGAZINE"
    var selectedDateFilter by remember { mutableStateOf<String?>(null) }
    var isAnnounceDialogOpen by remember { mutableStateOf(false) }
    var previewEdition by remember { mutableStateOf<UpcomingEditionEntity?>(null) }

    // Calendar dates in Sept 2026
    val calendarDays = remember {
        listOf(
            Triple("11", "Fri", "2026-09-11"),
            Triple("12", "Sat", "2026-09-12"),
            Triple("13", "Sun", "2026-09-13"),
            Triple("14", "Mon", "2026-09-14"),
            Triple("15", "Tue", "2026-09-15"),
            Triple("16", "Wed", "2026-09-16"),
            Triple("17", "Thu", "2026-09-17"),
            Triple("18", "Fri", "2026-09-18"),
            Triple("19", "Sat", "2026-09-19"),
            Triple("20", "Sun", "2026-09-20"),
            Triple("21", "Mon", "2026-09-21"),
            Triple("22", "Tue", "2026-09-22")
        )
    }

    val filteredEditions = remember(upcomingEditions, selectedFilter, selectedDateFilter) {
        upcomingEditions.filter { edition ->
            val matchesType = when (selectedFilter) {
                "FOLLOWED" -> edition.isSubscribed
                "BROADSHEET" -> edition.editionType == "BROADSHEET" || edition.editionType == "WEEKEND_SPECIAL"
                "MAGAZINE" -> edition.editionType == "MAGAZINE" || edition.editionType == "INVESTIGATION"
                else -> true
            }
            val matchesDate = selectedDateFilter == null || edition.releaseDate == selectedDateFilter
            matchesType && matchesDate
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("newsstand_calendar_view"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Masthead & Schedule Announce Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C26)),
                border = BorderStroke(1.dp, Color(0xFF263346))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NeonCyan.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Publication Release Calendar",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Announcements of upcoming drops from followed presses",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isAnnounceDialogOpen = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color(0xFF003544)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_announce_edition")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Announce", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date strip selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEPTEMBER 2026 DISPATCHES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonCyan
                        )
                        if (selectedDateFilter != null) {
                            Text(
                                text = "Clear Date",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber,
                                modifier = Modifier
                                    .clickable { selectedDateFilter = null }
                                    .testTag("clear_date_filter")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(calendarDays) { (dayNum, dayName, dateStr) ->
                            val isSelected = selectedDateFilter == dateStr
                            val hasDrop = upcomingEditions.any { it.releaseDate == dateStr }
                            val isFollowedDrop = upcomingEditions.any { it.releaseDate == dateStr && it.isSubscribed }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    isSelected -> NeonCyan
                                    isFollowedDrop -> Color(0xFF1E2B3E)
                                    else -> Color(0xFF181D26)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else if (isFollowedDrop) Color(0xFF00D2FF).copy(alpha = 0.4f) else Color(0xFF263346)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedDateFilter = if (isSelected) null else dateStr
                                    }
                                    .testTag("cal_day_$dayNum")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = if (isSelected) Color(0xFF003544) else Color.Gray
                                    )
                                    Text(
                                        text = dayNum,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color(0xFF003544) else Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    if (hasDrop) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color(0xFF003544) else if (isFollowedDrop) NeonCyan else WarmAmber)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(5.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    "FOLLOWED" to "⭐ Followed Publications (${upcomingEditions.count { it.isSubscribed }})",
                    "ALL" to "All Upcoming (${upcomingEditions.size})",
                    "BROADSHEET" to "📰 Broadsheets",
                    "MAGAZINE" to "📖 Magazines & Reviews"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedFilter = key
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color(0xFF003544),
                            containerColor = Color(0xFF161A22),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color(0xFF263346)),
                        modifier = Modifier.testTag("filter_cal_$key")
                    )
                }
            }
        }

        // Section header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedFilter == "FOLLOWED") "ANNOUNCED EDITIONS • FOLLOWED BY YOU" else "ALL ANNOUNCED EDITIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${filteredEditions.size} editions scheduled",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }
        }

        // Upcoming Editions List
        if (filteredEditions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141820)),
                    border = BorderStroke(1.dp, Color(0xFF222B3A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📅", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Upcoming Editions Scheduled",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try picking another date or switch filter to 'All Upcoming'.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                selectedFilter = "ALL"
                                selectedDateFilter = null
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reset Calendar Filter")
                        }
                    }
                }
            }
        } else {
            items(filteredEditions, key = { it.id }) { edition ->
                UpcomingEditionCard(
                    edition = edition,
                    onToggleReminder = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleReminder(edition)
                    },
                    onToggleSubscription = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleSubscription(edition)
                    },
                    onPreview = {
                        previewEdition = edition
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Schedule / Announce Edition Dialog
    if (isAnnounceDialogOpen) {
        AnnounceEditionDialog(
            onDismiss = { isAnnounceDialogOpen = false },
            onConfirm = { title, type, vol, date, dayLabel, time, head, teaser, notes, color ->
                onAnnounceEdition(title, type, vol, date, dayLabel, time, head, teaser, notes, color) {
                    isAnnounceDialogOpen = false
                }
            }
        )
    }

    // Front-Page Teaser Preview Dialog
    previewEdition?.let { edition ->
        AlertDialog(
            onDismissRequest = { previewEdition = null },
            confirmButton = {
                Button(
                    onClick = { previewEdition = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Close Preview")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗞️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = edition.publicationTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${edition.volumeIssue} • Scheduled: ${edition.releaseDayLabel} at ${edition.releaseTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = edition.coverHeadline,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = edition.leadTeaser,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD1D5DB)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E2634),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "EDITOR'S ADVANCE DISPATCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = edition.editorNotes,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun UpcomingEditionCard(
    edition: UpcomingEditionEntity,
    onToggleReminder: () -> Unit,
    onToggleSubscription: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upcoming_edition_card_${edition.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171C25)),
        border = BorderStroke(
            1.dp,
            if (edition.isSubscribed) Color(edition.bannerColorHex).copy(alpha = 0.35f) else Color(0xFF242C3A)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header strip with banner color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(edition.bannerColorHex).copy(alpha = 0.28f),
                                Color(0xFF171C25)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (edition.editionType.contains("MAGAZINE")) Icons.Default.MenuBook else Icons.Default.Newspaper,
                            contentDescription = null,
                            tint = Color(edition.bannerColorHex),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = edition.publicationTitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color(edition.bannerColorHex)
                        )
                    }

                    // Followed badge / toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (edition.isSubscribed) WarmAmber.copy(alpha = 0.2f) else Color(0xFF263042),
                        border = BorderStroke(
                            1.dp,
                            if (edition.isSubscribed) WarmAmber.copy(alpha = 0.6f) else Color.Transparent
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleSubscription() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (edition.isSubscribed) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (edition.isSubscribed) WarmAmber else Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (edition.isSubscribed) "Following" else "Follow",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (edition.isSubscribed) WarmAmber else Color.LightGray
                            )
                        }
                    }
                }
            }

            // Body content
            Column(modifier = Modifier.padding(16.dp)) {
                // Timing & release badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F2B38),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "⏰ ${edition.releaseDayLabel} • ${edition.releaseTime}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = edition.volumeIssue,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Headline
                Text(
                    text = edition.coverHeadline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Teaser
                Text(
                    text = edition.leadTeaser,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC7CBD3),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Special section pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF222A38)
                ) {
                    Text(
                        text = "📌 ${edition.specialSection}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preview button
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, Color(0xFF2A374D)),
                        modifier = Modifier.testTag("btn_preview_edition_${edition.id}")
                    ) {
                        Text(
                            text = "Preview Teaser",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.LightGray
                        )
                    }

                    // Notify Me / Reminder toggle
                    Button(
                        onClick = onToggleReminder,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (edition.isReminderSet) NeonCyan else Color(0xFF242F40),
                            contentColor = if (edition.isReminderSet) Color(0xFF003544) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_notify_edition_${edition.id}")
                    ) {
                        Icon(
                            imageVector = if (edition.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (edition.isReminderSet) "Notification Set" else "Notify Me",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnounceEditionDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        type: String,
        volume: String,
        releaseDate: String,
        dayLabel: String,
        releaseTime: String,
        headline: String,
        teaser: String,
        notes: String,
        colorHex: Long
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("BROADSHEET") }
    var volume by remember { mutableStateOf("Vol. 1, Issue 2") }
    var releaseDate by remember { mutableStateOf("2026-09-14") }
    var dayLabel by remember { mutableStateOf("Monday • Sept 14") }
    var releaseTime by remember { mutableStateOf("07:00 AM") }
    var headline by remember { mutableStateOf("") }
    var teaser by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📢", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Announce Upcoming Edition",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Announce a forthcoming newspaper or magazine drop in the community calendar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Publication Name (e.g. The River Gazette)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == "BROADSHEET",
                            onClick = { selectedType = "BROADSHEET" },
                            label = { Text("Broadsheet") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedType == "MAGAZINE",
                            onClick = { selectedType = "MAGAZINE" },
                            label = { Text("Magazine") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = volume,
                        onValueChange = { volume = it },
                        label = { Text("Volume & Issue (e.g. Vol. 2, Issue 1)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = dayLabel,
                        onValueChange = { dayLabel = it },
                        label = { Text("Day / Date Label (e.g. Monday • Sept 14)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = releaseTime,
                        onValueChange = { releaseTime = it },
                        label = { Text("Drop Time (e.g. 06:30 AM)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        label = { Text("Cover Headline Preview") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = teaser,
                        onValueChange = { teaser = it },
                        label = { Text("Editorial Teaser Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Editor Notes / Pullout Section") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && headline.isNotBlank()) {
                        onConfirm(
                            title,
                            selectedType,
                            volume,
                            releaseDate,
                            dayLabel,
                            releaseTime,
                            headline,
                            teaser.ifEmpty { "Exclusive community report." },
                            notes.ifEmpty { "Printed via Townsquare Citizen Guild." },
                            if (selectedType == "MAGAZINE") 0xFF9D4EDD else 0xFF00D2FF
                        )
                    }
                },
                enabled = title.isNotBlank() && headline.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
            ) {
                Text("Schedule Drop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
