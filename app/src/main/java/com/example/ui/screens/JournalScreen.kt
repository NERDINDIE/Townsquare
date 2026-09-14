package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.model.JournalEditionEntity
import com.example.data.model.NotepadDraftEntity
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class BroadsheetTemplate(val title: String, val description: String, val badge: String) {
    CLASSIC_BROADSHEET("Classic Broadsheet", "Timeless double-rule masthead, ornate drop caps and serif columns", "Traditional"),
    MODERN_GAZETTE("Modern Gazette", "Clean geometric masthead with high-contrast headline banners", "Contemporary"),
    VINTAGE_PRESS("Vintage Press", "Distressed typewriter type with nostalgic 1920s print shop feel", "Heritage"),
    EXECUTIVE_DISPATCH("Executive Dispatch", "Navy & gold prestige typography with financial digest rules", "Prestige")
}

val BroadsheetColors = listOf(
    0xFFD4A373 to "Amber Ochre",
    0xFF00D2FF to "Civic Cyan",
    0xFF4E54C8 to "Indigo Press",
    0xFF2A9D8F to "Forest Teal",
    0xFFE76F51 to "Terracotta Red",
    0xFF4A4E69 to "Charcoal Slate"
)

@Composable
fun JournalScreen(
    journalEditions: List<JournalEditionEntity>,
    notepadDrafts: List<NotepadDraftEntity> = emptyList(),
    notepadFilter: String = "ALL",
    onSelectNotepadFilter: (String) -> Unit = {},
    onSaveNotepadDraft: (NotepadDraftEntity) -> Unit = {},
    onToggleNotepadDraftStar: (NotepadDraftEntity) -> Unit = {},
    onDeleteNotepadDraft: (Long) -> Unit = {},
    onConvertDraftToJournal: (NotepadDraftEntity) -> Unit = {},
    onOpenFlipbook: (JournalEditionEntity) -> Unit,
    onCreateEdition: (
        title: String,
        motto: String,
        volume: Int,
        issue: Int,
        date: String,
        templateStyle: String,
        bannerColor: Long,
        leadHeadline: String,
        leadSubheadline: String,
        leadBody: String,
        leadAuthor: String,
        secondaryHeadline: String,
        secondaryBody: String,
        editorialNotes: String,
        communityBulletin: String,
        onSuccess: (Long) -> Unit
    ) -> Unit,
    onDeleteEdition: (Long) -> Unit,
    onOpenSidebar: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Archive Vault, 1: Compose New Issue, 2: Idea & Draft Notepad
    val haptic = LocalHapticFeedback.current

    // Form fields for new edition maker
    val currentDateStr = remember {
        SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(Date())
    }

    var newspaperTitle by remember { mutableStateOf("The Townsquare Gazette") }
    var motto by remember { mutableStateOf("All The Town's News In Print • Est. 2026") }
    var volumeNumber by remember { mutableIntStateOf(1) }
    var issueNumber by remember { mutableIntStateOf((journalEditions.maxOfOrNull { it.issueNumber } ?: 0) + 1) }
    var issueDate by remember { mutableStateOf(currentDateStr) }
    var selectedTemplate by remember { mutableStateOf(BroadsheetTemplate.CLASSIC_BROADSHEET) }
    var selectedColorHex by remember { mutableLongStateOf(0xFFD4A373) }

    var leadHeadline by remember { mutableStateOf("") }
    var leadSubheadline by remember { mutableStateOf("") }
    var leadAuthor by remember { mutableStateOf("Editor in Chief") }
    var leadArticleBody by remember { mutableStateOf("") }

    var secondaryHeadline by remember { mutableStateOf("") }
    var secondaryArticleBody by remember { mutableStateOf("") }

    var editorialNotes by remember { mutableStateOf("") }
    var communityBulletin by remember { mutableStateOf("Farmers Market Saturday 9 AM • Town Library Book Sale • Weather: Sunny 74°F") }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var newlyCreatedEditionId by remember { mutableLongStateOf(0L) }
    var deleteCandidateId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("journal_screen")
    ) {
        // JOURNAL HEADER
        TownsquareTopBar(
            title = "Journal Studio & Press",
            subtitle = "Craft Broadsheets, Drafts & Archive",
            onOpenSidebar = onOpenSidebar,
            actions = {
                Surface(
                    color = WarmAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${journalEditions.size} Archived",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        )

        // TABS: ARCHIVE VAULT vs COMPOSE NEW vs IDEA NOTEPAD
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1B1813),
            contentColor = WarmAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = WarmAmber,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Archive (${journalEditions.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.testTag("journal_tab_archive")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Compose", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.testTag("journal_tab_compose")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Drafts (${notepadDrafts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                modifier = Modifier.testTag("journal_tab_drafts")
            )
        }

        // CONTENT BODY
        if (selectedTab == 0) {
            // TAB 0: PAST EDITIONS ARCHIVE
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "THE BROADSHEET VAULT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                                color = WarmAmber
                            )
                            Text(
                                text = "All printed editions archived with full flipbook support",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }

                        Button(
                            onClick = {
                                selectedTab = 1
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarmAmber,
                                contentColor = Color(0xFF2E1C00)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("archive_new_edition_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Edition", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (journalEditions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1914)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📰", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Newspapers Archived Yet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Switch to the Compose tab to set your masthead, draft your lead story, and print your inaugural edition!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { selectedTab = 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF2E1C00))
                                ) {
                                    Text("Compose First Edition")
                                }
                            }
                        }
                    }
                } else {
                    items(journalEditions, key = { it.id }) { edition ->
                        ArchivedEditionCard(
                            edition = edition,
                            onOpenFlipbook = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onOpenFlipbook(edition)
                            },
                            onNewFromThis = {
                                newspaperTitle = edition.newspaperTitle
                                motto = edition.motto
                                volumeNumber = edition.volumeNumber
                                issueNumber = edition.issueNumber + 1
                                selectedTemplate = BroadsheetTemplate.values().firstOrNull { it.name == edition.templateStyle }
                                    ?: BroadsheetTemplate.CLASSIC_BROADSHEET
                                selectedColorHex = edition.bannerColorHex
                                selectedTab = 1
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDelete = { deleteCandidateId = edition.id },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        } else if (selectedTab == 1) {
            // TAB 1: COMPOSE NEW ISSUE (THE NEWSPAPER PRESS)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
            ) {
                // MASTHEAD SETUP SECTION
                item {
                    Text(
                        text = "1. MASTHEAD & PUBLICATION SETTINGS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B15)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF332B1E))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Newspaper Title
                            OutlinedTextField(
                                value = newspaperTitle,
                                onValueChange = { newspaperTitle = it },
                                label = { Text("Newspaper Masthead Title") },
                                placeholder = { Text("e.g. The Townsquare Chronicle") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_newspaper_title"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Motto / Tagline
                            OutlinedTextField(
                                value = motto,
                                onValueChange = { motto = it },
                                label = { Text("Masthead Motto / Tagline") },
                                placeholder = { Text("e.g. All the Town's News In Print") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_newspaper_motto"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Vol, Issue, Date Row
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = volumeNumber.toString(),
                                    onValueChange = { volumeNumber = it.toIntOrNull() ?: 1 },
                                    label = { Text("Volume") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .testTag("input_newspaper_volume"),
                                    colors = journalTextFieldColors()
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = issueNumber.toString(),
                                    onValueChange = { issueNumber = it.toIntOrNull() ?: 1 },
                                    label = { Text("Issue #") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .testTag("input_newspaper_issue"),
                                    colors = journalTextFieldColors()
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = issueDate,
                                    onValueChange = { issueDate = it },
                                    label = { Text("Issue Date") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1.6f)
                                        .testTag("input_newspaper_date"),
                                    colors = journalTextFieldColors()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Broadsheet Template Style
                            Text(
                                text = "Broadsheet Typography Style",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(BroadsheetTemplate.values()) { tmpl ->
                                    val isSelected = tmpl == selectedTemplate
                                    Surface(
                                        color = if (isSelected) WarmAmber else Color(0xFF2B251D),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .clickable {
                                                selectedTemplate = tmpl
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .testTag("template_${tmpl.name}")
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            Text(
                                                text = tmpl.title,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) Color(0xFF2E1C00) else Color.White
                                            )
                                            Text(
                                                text = tmpl.badge,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSelected) Color(0xFF4A3200) else Color.Gray
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Accent Ink Color Selector
                            Text(
                                text = "Press Ink / Accent Tone",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(BroadsheetColors) { (hex, name) ->
                                    val isSelected = selectedColorHex == hex
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(hex))
                                            .clickable {
                                                selectedColorHex = hex
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            }
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) Color.White else Color.Transparent,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // FRONT PAGE LEAD STORY SECTION
                item {
                    Text(
                        text = "2. FRONT PAGE LEAD STORY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181D26)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF222B3D))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            OutlinedTextField(
                                value = leadHeadline,
                                onValueChange = { leadHeadline = it },
                                label = { Text("Lead Headline (Required)") },
                                placeholder = { Text("e.g. Townsquare Council Approves Green Canopy Initiative") },
                                singleLine = false,
                                maxLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_lead_headline"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = leadSubheadline,
                                onValueChange = { leadSubheadline = it },
                                label = { Text("Subheadline / Lead Deck") },
                                placeholder = { Text("e.g. Unanimous civic vote clears 4.2 miles of pedestrian greenways.") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_lead_subheadline"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = leadAuthor,
                                onValueChange = { leadAuthor = it },
                                label = { Text("Lead Author / Byline") },
                                placeholder = { Text("e.g. Lillian Vance, Civic Desk") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_lead_author"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = leadArticleBody,
                                onValueChange = { leadArticleBody = it },
                                label = { Text("Lead Article Body Text (Required)") },
                                placeholder = { Text("Write the primary story for your front page spread. Use double line breaks between paragraphs for broadsheet columns...") },
                                minLines = 5,
                                maxLines = 12,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_lead_body"),
                                colors = journalTextFieldColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // SECONDARY ARTICLE SECTION (Page 2)
                item {
                    Text(
                        text = "3. PAGE 2 FEATURE & INVESTIGATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C22)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            OutlinedTextField(
                                value = secondaryHeadline,
                                onValueChange = { secondaryHeadline = it },
                                label = { Text("Secondary Headline (Page 2)") },
                                placeholder = { Text("e.g. Local Organic Farmers Market Sets 10-Year Record") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_secondary_headline"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = secondaryArticleBody,
                                onValueChange = { secondaryArticleBody = it },
                                label = { Text("Secondary Story Body Text") },
                                placeholder = { Text("In-depth analysis, neighborhood interview, or business feature...") },
                                minLines = 3,
                                maxLines = 8,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_secondary_body"),
                                colors = journalTextFieldColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // EDITORIAL NOTES & TOWN ALMANAC (Pages 3 & 4)
                item {
                    Text(
                        text = "4. EDITORIAL DESK & TOWN ALMANAC (Pages 3 & 4)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C22)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            OutlinedTextField(
                                value = editorialNotes,
                                onValueChange = { editorialNotes = it },
                                label = { Text("From the Editor's Desk / Opinion Column") },
                                placeholder = { Text("A personal note to your readers on the purpose of this edition...") },
                                minLines = 2,
                                maxLines = 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_editorial_notes"),
                                colors = journalTextFieldColors()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = communityBulletin,
                                onValueChange = { communityBulletin = it },
                                label = { Text("Town Almanac & Bulletins (Separate with •)") },
                                placeholder = { Text("e.g. Library Book Club • Town Orchestra Concert • Forecast: 72°F") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_community_bulletin"),
                                colors = journalTextFieldColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // PRINT & ARCHIVE ACTION BUTTON
                item {
                    val canPrint = leadHeadline.isNotBlank() && leadArticleBody.isNotBlank()

                    Button(
                        onClick = {
                            if (canPrint) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCreateEdition(
                                    newspaperTitle,
                                    motto,
                                    volumeNumber,
                                    issueNumber,
                                    issueDate,
                                    selectedTemplate.name,
                                    selectedColorHex,
                                    leadHeadline,
                                    leadSubheadline,
                                    leadArticleBody,
                                    leadAuthor,
                                    secondaryHeadline,
                                    secondaryArticleBody,
                                    editorialNotes,
                                    communityBulletin
                                ) { newId ->
                                    newlyCreatedEditionId = newId
                                    showSuccessDialog = true
                                }
                            }
                        },
                        enabled = canPrint,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("print_and_archive_edition_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmAmber,
                            contentColor = Color(0xFF2E1C00),
                            disabledContainerColor = Color(0xFF2E261B),
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Print & Archive Edition to Press Vault",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (canPrint) "Edition will be archived permanently in the vault and placed on the Townsquare Newsstand" else "Fill in Lead Headline and Article Body to print edition",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canPrint) Color.LightGray else Color(0xFFE57373),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else if (selectedTab == 2) {
            // TAB 2: MULTIMEDIA NOTEPAD (IDEA & DRAFT HOLDER)
            MultimediaNotepadScreen(
                drafts = notepadDrafts,
                selectedFilter = notepadFilter,
                onSelectFilter = onSelectNotepadFilter,
                onSaveDraft = onSaveNotepadDraft,
                onToggleStar = onToggleNotepadDraftStar,
                onDeleteDraft = onDeleteNotepadDraft,
                onConvertToJournal = { draft ->
                    leadHeadline = draft.title
                    leadArticleBody = draft.bodyText
                    if (draft.quoteAttribution.isNotEmpty()) {
                        editorialNotes = "Quote: \"${draft.quoteAttribution}\""
                    }
                    onConvertDraftToJournal(draft)
                    selectedTab = 1
                },
                onBackToJournal = { selectedTab = 0 },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // CELEBRATION / SUCCESS DIALOG
    if (showSuccessDialog) {
        val createdEdition = journalEditions.firstOrNull { it.id == newlyCreatedEditionId }
            ?: journalEditions.firstOrNull()

        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📰", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Newspaper Printed & Archived!")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Your edition of \"$newspaperTitle\" (Vol. $volumeNumber, Issue $issueNumber) has been hot off the press and archived permanently into the Townsquare Vault.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "It is now available on the Newsstand rack and ready for interactive haptic flipbook reading.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        selectedTab = 0 // Go back to archive
                        if (createdEdition != null) {
                            onOpenFlipbook(createdEdition)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF2E1C00))
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read in Flipbook")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    selectedTab = 0
                }) {
                    Text("View in Archive")
                }
            }
        )
    }

    // DELETE CONFIRMATION DIALOG
    if (deleteCandidateId != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidateId = null },
            title = { Text("Delete Archived Edition?") },
            text = { Text("Are you sure you want to remove this edition from the Townsquare Press Archive?") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteCandidateId?.let { onDeleteEdition(it) }
                        deleteCandidateId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidateId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Card displaying an archived newspaper edition in the Vault.
 */
@Composable
fun ArchivedEditionCard(
    edition: JournalEditionEntity,
    onOpenFlipbook: () -> Unit,
    onNewFromThis: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
            .clickable { onOpenFlipbook() }
            .testTag("archived_edition_${edition.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B15)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(edition.bannerColorHex).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Masthead header strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(edition.bannerColorHex))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = edition.newspaperTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        ),
                        color = Color(edition.bannerColorHex)
                    )
                }

                Text(
                    text = "Vol. ${edition.volumeNumber} • Issue #${edition.issueNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = Color(0xFF332B1E), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Headline
            Text(
                text = edition.leadHeadline,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 22.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (edition.leadSubheadline.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = edition.leadSubheadline,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lead excerpt
            Text(
                text = edition.leadArticleBody.take(160) + if (edition.leadArticleBody.length > 160) "..." else "",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif),
                color = Color(0xFFB0A89C),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFF332B1E), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer metadata & actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = edition.issueDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    Text(
                        text = "By ${edition.leadAuthor} • ${edition.circulationReads} readers",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNewFromThis,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Next Issue", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE57373), modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = onOpenFlipbook,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmAmber,
                            contentColor = Color(0xFF2E1C00)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("read_archive_flipbook_${edition.id}")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Flip Pages", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun journalTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF14120E),
    unfocusedContainerColor = Color(0xFF14120E),
    focusedBorderColor = WarmAmber,
    unfocusedBorderColor = Color(0xFF332B1E),
    focusedLabelColor = WarmAmber,
    unfocusedLabelColor = Color.Gray
)
