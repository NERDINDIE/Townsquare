package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import com.example.data.model.JournalEditionEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.data.model.RetailKioskEntity
import com.example.data.model.UpcomingEditionEntity
import com.example.ui.components.NewsstandCalendarView
import com.example.ui.components.NewsstandKioskMapView
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun NewsstandScreen(
    items: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    upcomingEditions: List<UpcomingEditionEntity> = emptyList(),
    retailKiosks: List<RetailKioskEntity> = emptyList(),
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onOpenFlipbookItem: (MediaItemEntity) -> Unit,
    onOpenFlipbookJournal: (JournalEditionEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onToggleEditionReminder: (UpcomingEditionEntity) -> Unit = {},
    onToggleEditionSubscription: (UpcomingEditionEntity) -> Unit = {},
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
    ) -> Unit = { _, _, _, _, _, _, _, _, _, _, _ -> },
    onReserveKioskCopy: (RetailKioskEntity, Int, () -> Unit) -> Unit = { _, _, _ -> },
    onToggleKioskFavorite: (RetailKioskEntity) -> Unit = {},
    onNavigateToJournal: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Rack, 1: Calendar, 2: Map
    val haptic = LocalHapticFeedback.current

    // Set of publication titles that the user follows / subscribes to
    val followedPublicationTitles = remember(upcomingEditions) {
        val subs = upcomingEditions.filter { it.isSubscribed }.map { it.publicationTitle }.toSet()
        if (subs.isEmpty()) setOf("The Daily Townsquare", "Metropolitan Sunday Chronicle", "Civic Observer") else subs
    }

    // Only e-newspapers and magazines
    val newspaperAndMagazines = remember(items) {
        items.filter { it.type == MediaType.NEWSPAPER_MAGAZINE.name }
    }

    // Main layout container with top sub-tab selector
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("newsstand_screen")
    ) {
        // Newsstand Top Masthead
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF161A22),
                            Color(0xFF0F1115)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📰", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Townsquare Newsstand",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Broadsheets, Periodicals, Drops & Kiosks",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                    }

                    // Shortcut to Journal Studio
                    Button(
                        onClick = onNavigateToJournal,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF242A38),
                            contentColor = WarmAmber
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("newsstand_journal_shortcut_button")
                    ) {
                        Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Journal Press", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3-Way Mode Segmented Bar: [ 🗞️ Press Rack ] | [ 📅 Release Calendar ] | [ 🗺️ Kiosk Map ]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B212D))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        "🗞️ Press Rack",
                        "📅 Calendar (${upcomingEditions.size})",
                        "🗺️ Store Map (${retailKiosks.size})"
                    )
                    tabs.forEachIndexed { index, title ->
                        val isSelected = activeSubTab == index
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = if (isSelected) NeonCyan else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    activeSubTab = index
                                }
                                .testTag("newsstand_tab_$index")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) Color(0xFF003544) else Color.LightGray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Body Content based on activeSubTab
        when (activeSubTab) {
            1 -> {
                NewsstandCalendarView(
                    upcomingEditions = upcomingEditions,
                    onToggleReminder = onToggleEditionReminder,
                    onToggleSubscription = onToggleEditionSubscription,
                    onAnnounceEdition = onAnnounceEdition,
                    modifier = Modifier.fillMaxSize()
                )
            }
            2 -> {
                NewsstandKioskMapView(
                    retailKiosks = retailKiosks,
                    followedPublications = followedPublicationTitles,
                    onReserveCopy = onReserveKioskCopy,
                    onToggleFavorite = onToggleKioskFavorite,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // Newsstand Rack (existing)
                NewsstandRackContent(
                    items = newspaperAndMagazines,
                    journalEditions = journalEditions,
                    upcomingEditions = upcomingEditions,
                    retailKiosks = retailKiosks,
                    selectedFilter = selectedFilter,
                    onFilterChange = onFilterChange,
                    onOpenFlipbookItem = onOpenFlipbookItem,
                    onOpenFlipbookJournal = onOpenFlipbookJournal,
                    onToggleLike = onToggleLike,
                    onToggleBookmark = onToggleBookmark,
                    onJumpToCalendar = { activeSubTab = 1 },
                    onJumpToMap = { activeSubTab = 2 },
                    onNavigateToJournal = onNavigateToJournal
                )
            }
        }
    }
}

@Composable
fun NewsstandRackContent(
    items: List<MediaItemEntity>,
    journalEditions: List<JournalEditionEntity>,
    upcomingEditions: List<UpcomingEditionEntity>,
    retailKiosks: List<RetailKioskEntity>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onOpenFlipbookItem: (MediaItemEntity) -> Unit,
    onOpenFlipbookJournal: (JournalEditionEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onJumpToCalendar: () -> Unit,
    onJumpToMap: () -> Unit,
    onNavigateToJournal: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current

    val filteredPublications = remember(items, journalEditions, selectedFilter, searchQuery) {
        val q = searchQuery.trim().lowercase()
        when (selectedFilter) {
            "NEWSPAPER" -> items.filter {
                (it.tags.contains("newspaper") || it.tags.contains("broadsheet") || it.title.contains("Chronicle", ignoreCase = true) || it.title.contains("Gazette", ignoreCase = true) || it.title.contains("Dispatch", ignoreCase = true)) &&
                        (q.isEmpty() || it.title.lowercase().contains(q) || it.bodyText.lowercase().contains(q))
            }
            "MAGAZINE" -> items.filter {
                (it.tags.contains("magazine") || it.tags.contains("design") || it.title.contains("Review", ignoreCase = true) || it.title.contains("Living", ignoreCase = true)) &&
                        (q.isEmpty() || it.title.lowercase().contains(q) || it.bodyText.lowercase().contains(q))
            }
            "MY_PRESS" -> emptyList()
            else -> items.filter {
                q.isEmpty() || it.title.lowercase().contains(q) || it.bodyText.lowercase().contains(q) || it.authorName.lowercase().contains(q)
            }
        }
    }

    val leadBroadsheet = remember(items) {
        items.firstOrNull { it.tags.contains("townsquare") || it.title.contains("Townsquare", ignoreCase = true) }
            ?: items.firstOrNull()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("newsstand_rack_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Quick Action Jump Cards for Calendar and Map
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Calendar Jump Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF17202C),
                    border = BorderStroke(1.dp, Color(0xFF26354A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onJumpToCalendar() }
                        .testTag("rack_jump_to_calendar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Release Calendar Announcements",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${upcomingEditions.size} upcoming editions announced from followed presses",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                        }
                        Text(
                            text = "View →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                }

                // Map Jump Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E26),
                    border = BorderStroke(1.dp, Color(0xFF332B3E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onJumpToMap() }
                        .testTag("rack_jump_to_map")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🗺️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Town Kiosks & Press Map",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${retailKiosks.size} retail stands stocking print editions of your publications",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmAmber
                                )
                            }
                        }
                        Text(
                            text = "Open Map →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WarmAmber
                        )
                    }
                }
            }
        }

        // KIOSK SEARCH FIELD & FILTERS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("newsstand_search_field"),
                    placeholder = { Text("Search broadsheets, headlines, and topics...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NeonCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1B1F2A),
                        unfocusedContainerColor = Color(0xFF1B1F2A),
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF2A3142)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Newsstand Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterOptions = listOf(
                        "ALL" to "All Publications",
                        "NEWSPAPER" to "📰 Broadsheets",
                        "MAGAZINE" to "📖 Magazines",
                        "MY_PRESS" to "✒️ Citizen Journals (${journalEditions.size})"
                    )

                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onFilterChange(key)
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color(0xFF003544),
                                containerColor = Color(0xFF171B24),
                                labelColor = Color.LightGray
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonCyan else Color(0xFF262E3E)
                            ),
                            modifier = Modifier.testTag("newsstand_filter_$key")
                        )
                    }
                }
            }
        }

        // LEAD BROADSHEET HERO SHOWCASE
        if (leadBroadsheet != null && selectedFilter != "MY_PRESS" && searchQuery.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TODAY'S LEAD BROADSHEET",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                            color = WarmAmber
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Haptic Flipbook Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Physical Broadsheet Folded Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOpenFlipbookItem(leadBroadsheet)
                            }
                            .testTag("lead_broadsheet_card"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2E7)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFDCD2C0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Newspaper Masthead Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "THE TOWNSQUARE CHRONICLE",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Serif,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color(0xFF1D1B18)
                                )
                                Text(
                                    text = leadBroadsheet.issueEdition.ifEmpty { "Morning Edition" },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF5A544A)
                                )
                            }

                            Divider(
                                color = Color(0xFF1D1B18),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            // Lead Headline
                            Text(
                                text = leadBroadsheet.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 22.sp
                                ),
                                color = Color(0xFF1D1B18),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = leadBroadsheet.subtitle.ifEmpty { leadBroadsheet.bodyText.take(120) + "..." },
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                                color = Color(0xFF4A443A),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Paper Fold Divider (embossed crease)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(Color(0xFFD6C8B2))
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "By ${leadBroadsheet.authorName}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Color(0xFF1D1B18)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("•", color = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${leadBroadsheet.readTimeMinutes} min read",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF5A544A)
                                    )
                                }

                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onOpenFlipbookItem(leadBroadsheet)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF003847),
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("open_lead_flipbook_button")
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Flip Pages", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }

        // USER JOURNAL EDITIONS SECTION (If in MY_PRESS or has editions)
        if (selectedFilter == "MY_PRESS" || (selectedFilter == "ALL" && journalEditions.isNotEmpty())) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏛️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MY JOURNAL PRESS ARCHIVE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = WarmAmber
                            )
                        }
                        Text(
                            text = "${journalEditions.size} Editions",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (journalEditions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E26)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🖨️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your Press Archive is Ready",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Draft your own front page broadsheet, print an edition, and it will appear here on the Townsquare newsstand.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToJournal,
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF2E1C00)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Compose First Newspaper")
                            }
                        }
                    }
                }
            } else {
                items(journalEditions, key = { "journal_${it.id}" }) { journal ->
                    JournalEditionRackCard(
                        journal = journal,
                        onOpenFlipbook = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOpenFlipbookJournal(journal)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // EDITORIAL NEWSSTAND RACK (Publications)
        if (selectedFilter != "MY_PRESS") {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "THE NEWSSTAND RACK",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                        Text(
                            text = "${filteredPublications.size} Issues Available",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any issue to flip open with realistic physics and broadsheet layout",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            items(filteredPublications, key = { it.id }) { item ->
                NewsstandRackItem(
                    item = item,
                    onOpenFlipbook = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenFlipbookItem(item)
                    },
                    onToggleLike = { onToggleLike(item) },
                    onToggleBookmark = { onToggleBookmark(item) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }
        }

        // FOOTER BANNER: INVITATION TO JOURNAL STUDIO
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToJournal() }
                    .testTag("newsstand_footer_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D222E)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WarmAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🖋️", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Become a Townsquare Publisher",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Craft personalized front pages, set vintage mastheads, and archive editions in the Journal studio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

/**
 * Newsstand publication item styled as a physical periodical with paper stack depth,
 * format badges, and instant e-flipbook launch.
 */
@Composable
fun NewsstandRackItem(
    item: MediaItemEntity,
    onOpenFlipbook: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMagazine = item.tags.contains("magazine") || item.title.contains("Review", ignoreCase = true) || item.title.contains("Living", ignoreCase = true)

    // Stacked paper effect: outer box with subtle bottom offset shadow
    Box(modifier = modifier) {
        // Stacked page underneath
        Box(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .height(14.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF282C37), shape = RoundedCornerShape(10.dp))
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .clickable { onOpenFlipbook() }
                .testTag("newsstand_item_${item.id}"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D24)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF2A2E3B))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Header badge row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isMagazine) ElectricIndigo.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isMagazine) "GLOSSY PERIODICAL" else "DAILY BROADSHEET",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isMagazine) ElectricIndigo else NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.issueEdition.ifEmpty { "Town Edition" },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "4 Pages • E-Flipbook",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = WarmAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 22.sp
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFF2A2E3B), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Bottom rack row: Author, Read Time, Flipbook Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = item.authorName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                        Text(
                            text = "${item.readTimeMinutes} min • ${item.channelName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (item.isBookmarked) NeonCyan else Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Button(
                            onClick = onOpenFlipbook,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF003847),
                                contentColor = NeonCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_flipbook_${item.id}")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Flipbook", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Journal edition card shown on the newsstand shelf.
 */
@Composable
fun JournalEditionRackCard(
    journal: JournalEditionEntity,
    onOpenFlipbook: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onOpenFlipbook() }
            .testTag("journal_rack_card_${journal.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF211E18)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏛️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = journal.newspaperTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        ),
                        color = WarmAmber
                    )
                }

                Surface(
                    color = WarmAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Vol. ${journal.volumeNumber} • Issue ${journal.issueNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = journal.leadHeadline,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (journal.leadSubheadline.isNotEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = journal.leadSubheadline,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFF383227), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${journal.issueDate} • By ${journal.leadAuthor}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )

                Button(
                    onClick = onOpenFlipbook,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmAmber,
                        contentColor = Color(0xFF2E1C00)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_journal_flipbook_${journal.id}")
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read Edition", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
