package com.example.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CollectionItem(
    val id: String,
    val name: String,
    val category: String, // Antiques, Broadsheets, Stamps & Postcards, Rare Vinyl, Transit Tokens
    val rarity: String, // Common, Rare, Ultra-Rare, Legendary Artifact
    val condition: String, // Mint, Near-Mint, Restored, Good
    val estimatedValueCredits: Int,
    val acquiredDate: String,
    val districtFound: String,
    val storyNotes: String,
    val iconEmoji: String,
    val isAppraised: Boolean = true
)

@Composable
fun CollectionTrackerSection(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var isAddItemOpen by remember { mutableStateOf(false) }
    var viewingItem by remember { mutableStateOf<CollectionItem?>(null) }
    var successToast by remember { mutableStateOf<String?>(null) }

    // New item form state
    var newName by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Antiques & Curios") }
    var newRarity by remember { mutableStateOf("Rare") }
    var newCondition by remember { mutableStateOf("Near-Mint") }
    var newValue by remember { mutableStateOf("350") }
    var newDistrict by remember { mutableStateOf("Old Town Bazaar") }
    var newNotes by remember { mutableStateOf("") }

    var items by remember {
        mutableStateOf(
            listOf(
                CollectionItem(
                    id = "col_1",
                    name = "1934 Art Deco Pocket Chronometer",
                    category = "Antiques & Curios",
                    rarity = "Legendary Artifact",
                    condition = "Restored",
                    estimatedValueCredits = 1200,
                    acquiredDate = "2026-08-14",
                    districtFound = "Grand Central Concourse",
                    storyNotes = "Original brass mechanical movement from the historic station master clock tower. Keep calibrated at 18,000 vph.",
                    iconEmoji = "⏱️"
                ),
                CollectionItem(
                    id = "col_2",
                    name = "Broadsheet Inaugural First Edition (Oct 2026)",
                    category = "Broadsheet Archives",
                    rarity = "Ultra-Rare",
                    condition = "Mint",
                    estimatedValueCredits = 650,
                    acquiredDate = "2026-10-01",
                    districtFound = "Press Room Kiosk #1",
                    storyNotes = "Uncut print run featuring the Waterfront Promenade panoramic foldout. Preserved in mylar sleeve.",
                    iconEmoji = "📰"
                ),
                CollectionItem(
                    id = "col_3",
                    name = "Line 3 Inaugural Brass Transit Token",
                    category = "Transit Tokens",
                    rarity = "Rare",
                    condition = "Near-Mint",
                    estimatedValueCredits = 280,
                    acquiredDate = "2026-09-12",
                    districtFound = "Metro Depot Turnstile",
                    storyNotes = "Numbered #0442 of the original commemorative run minted for the express line opening.",
                    iconEmoji = "🪙"
                ),
                CollectionItem(
                    id = "col_4",
                    name = "Harbor Lighthouse Philatelic Stamp Sheet",
                    category = "Stamps & Postcards",
                    rarity = "Rare",
                    condition = "Mint",
                    estimatedValueCredits = 420,
                    acquiredDate = "2026-07-20",
                    districtFound = "Postal Mailbox Exchange",
                    storyNotes = "Gum intact, zero hinge marks. Depicts the twin lanterns over North Pier.",
                    iconEmoji = "📮"
                ),
                CollectionItem(
                    id = "col_5",
                    name = "Harbor Serenades Vinyl Acetate Test Pressing",
                    category = "Rare Vinyl & Cassettes",
                    rarity = "Ultra-Rare",
                    condition = "Near-Mint",
                    estimatedValueCredits = 890,
                    acquiredDate = "2026-09-24",
                    districtFound = "City Soundwaves Vault",
                    storyNotes = "Signed by the lead cellist of the Civic Philharmonic. 33 1/3 RPM mono master.",
                    iconEmoji = "🎵"
                )
            )
        )
    }

    val categories = listOf("ALL", "Antiques & Curios", "Broadsheet Archives", "Transit Tokens", "Stamps & Postcards", "Rare Vinyl & Cassettes")
    val totalValue = items.sumOf { it.estimatedValueCredits }

    val filteredItems = items.filter {
        selectedCategory == "ALL" || it.category == selectedCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("collection_tracker_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Vault Valuation Summary Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WarmAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏺", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Citizen Artifact Vault",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${items.size} Verified Collectibles in Archive",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    // Total Valuation
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$totalValue Credits",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "PORTFOLIO VALUE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category completion progress
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Broadsheet Gazette Archive", fontSize = 11.sp, color = DarkTextSecondary)
                        Text("18 / 24 Issues (75%)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Historic Transit Tokens & Passes", fontSize = 11.sp, color = DarkTextSecondary)
                        Text("6 / 8 Tokens (75%)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { 0.75f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = WarmAmber,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }

        // Action row: Category filter + Add Collectible
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Collection Categories",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { isAddItemOpen = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Artifact", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Category Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (successToast != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(successToast!!, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
                    IconButton(onClick = { successToast = null }, modifier = Modifier.size(18.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Collection Items Grid / List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredItems.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingItem = item }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(item.iconEmoji, fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (item.rarity) {
                                        "Legendary Artifact" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                        "Ultra-Rare" -> Color(0xFFA855F7).copy(alpha = 0.2f)
                                        else -> NeonCyan.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = item.rarity,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when (item.rarity) {
                                            "Legendary Artifact" -> Color(0xFFF59E0B)
                                            "Ultra-Rare" -> Color(0xFFA855F7)
                                            else -> NeonCyan
                                        },
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "${item.category} • Condition: ${item.condition}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("📍 ${item.districtFound}", fontSize = 10.sp, color = DarkTextMuted)
                                Text(
                                    "${item.estimatedValueCredits} Credits",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // View Item Details Dialog
        if (viewingItem != null) {
            val itm = viewingItem!!
            AlertDialog(
                onDismissRequest = { viewingItem = null },
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(itm.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(itm.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Category: ${itm.category}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Text("Rarity Tier: ${itm.rarity} • Condition: ${itm.condition}", fontSize = 12.sp)
                        Text("Estimated Value: ${itm.estimatedValueCredits} Credits", fontSize = 13.sp, fontWeight = FontWeight.Black, color = WarmAmber)
                        Text("Acquired: ${itm.acquiredDate} in ${itm.districtFound}", fontSize = 11.sp, color = DarkTextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(itm.storyNotes, fontSize = 12.sp, modifier = Modifier.padding(10.dp), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewingItem = null }) {
                        Text("Close", color = NeonCyan)
                    }
                }
            )
        }

        // Add Collectible Dialog
        if (isAddItemOpen) {
            AlertDialog(
                onDismissRequest = { isAddItemOpen = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Log New Collectible to Vault", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Artifact Name e.g. Vintage Camera") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            label = { Text("Appraised Value (Credits)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDistrict,
                            onValueChange = { newDistrict = it },
                            label = { Text("Location / District Found") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newNotes,
                            onValueChange = { newNotes = it },
                            label = { Text("Historical Notes / Condition Details") },
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newItem = CollectionItem(
                                id = "col_${System.currentTimeMillis()}",
                                name = newName.ifBlank { "Rare Townsquare Artifact" },
                                category = newCategory,
                                rarity = newRarity,
                                condition = newCondition,
                                estimatedValueCredits = newValue.toIntOrNull() ?: 300,
                                acquiredDate = "2026-10-04",
                                districtFound = newDistrict,
                                storyNotes = newNotes.ifBlank { "Logged into citizen collection vault." },
                                iconEmoji = "🏺"
                            )
                            items = listOf(newItem) + items
                            isAddItemOpen = false
                            successToast = "Added '${newItem.name}' to personal vault!"
                            newName = ""
                            newNotes = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Save to Vault", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddItemOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
