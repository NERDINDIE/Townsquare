package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

data class ChildProfile(val name: String, val school: String, val grade: String)

@Composable
fun PlaygroundScreen(
    modifier: Modifier = Modifier,
    onSwitchProfile: (String) -> Unit = {},
    onOpenSidebar: () -> Unit = {}
) {
    BackHandler { onSwitchProfile("TOWNSQUARE") }

    val profiles = listOf(
        ChildProfile("Leo", "Oak Creek Elementary", "4th Grade"),
        ChildProfile("Mia", "Oak Creek Elementary", "2nd Grade")
    )
    var selectedProfileIndex by remember { mutableStateOf(0) }
    var showProfileDropdown by remember { mutableStateOf(false) }
    var isSchoolPaperOpen by remember { mutableStateOf(false) }
    var isPlaygroundSidebarOpen by remember { mutableStateOf(false) }
    val activeProfile = profiles[selectedProfileIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Child Profile Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onOpenSidebar,
                            modifier = Modifier.testTag("playground_sidebar_button")
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Sidebar")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.ChildCare, contentDescription = null, tint = Color(0xFF332A00), modifier = Modifier.size(26.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${activeProfile.name}'s Playground",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${activeProfile.school} • ${activeProfile.grade}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmAmber
                            )
                        }
                    }
                    
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showProfileDropdown = true }
                                .testTag("playground_profile_dropdown_trigger")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Switch",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = showProfileDropdown,
                            onDismissRequest = { showProfileDropdown = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            profiles.forEachIndexed { index, profile ->
                                DropdownMenuItem(
                                    text = { Text("Child: ${profile.name} (${profile.grade})", color = MaterialTheme.colorScheme.onSurface) },
                                    onClick = {
                                        selectedProfileIndex = index
                                        showProfileDropdown = false
                                    }
                                )
                            }
                            HorizontalDivider(color = DarkBorder)
                            DropdownMenuItem(
                                text = { Text("🏛️ Switch to Townsquare Profile", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    showProfileDropdown = false
                                    onSwitchProfile("TOWNSQUARE")
                                },
                                modifier = Modifier.testTag("switch_to_townsquare_menu_item")
                            )
                            DropdownMenuItem(
                                text = { Text("👤 Change Profile / Log Out", color = MaterialTheme.colorScheme.secondary) },
                                onClick = {
                                    showProfileDropdown = false
                                    onSwitchProfile("CHOOSE")
                                }
                            )
                        }
                    }
                }

                // Quick Switch Bar back to Townsquare
                Surface(
                    color = Color(0xFF1E2838),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSwitchProfile("TOWNSQUARE") }
                        .testTag("playground_switch_to_townsquare_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Return to regular Townsquare editorial profile",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonCyan
                        ) {
                            Text(
                                text = "SWITCH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = Color(0xFF003544),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // School Media: Radio Station & Publication Submissions
            item {
                com.example.ui.screens.playground.PlaygroundSchoolMediaFeatures(
                    studentName = activeProfile.name,
                    schoolName = activeProfile.school
                )
            }

            // School Announcements
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "School Announcements",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Science Fair Next Week! 🔬",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Don't forget to bring your project boards to the gym on Monday morning.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // School Newspaper
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = WarmAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "The Oak Creek Chronicle",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Written by students, for students",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Newspaper Articles
                        NewspaperArticle(
                            title = "4th Graders Plant New Garden",
                            author = "By Sarah M.",
                            content = "On Tuesday, Mrs. Smith's class planted tomatoes and carrots in the school courtyard. We can't wait to see them grow!"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NewspaperArticle(
                            title = "Cafeteria Adds Pizza Fridays",
                            author = "By James T.",
                            content = "Great news! The cafeteria will now serve pizza every Friday. Make sure to get in line early!"
                        )
                    }
                }
            }

            // Student Literary Magazine
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PhotoAlbum, contentDescription = null, tint = Color(0xFFE91E63))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Creative Corner Magazine",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE91E63)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Poetry, stories, and art from our students",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        NewspaperArticle(
                            title = "The Magic Treehouse (Short Story)",
                            author = "By Emma W., 3rd Grade",
                            content = "Once upon a time, there was a treehouse that could travel anywhere in time..."
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NewspaperArticle(
                            title = "Spring Breeze (Poem)",
                            author = "By Alex C., 5th Grade",
                            content = "The wind blows softly through the leaves,\nA gentle song the forest weaves..."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewspaperArticle(title: String, author: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = author,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = DarkBorder)
    }
}
