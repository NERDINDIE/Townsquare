package com.example.ui.components.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun CommunityPublicLibrarySection(
    onPlayInPlayer: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val vaultItems by PhysicalMediaRepository.vaultItems.collectAsState()
    var selectedFormatFilter by remember { mutableStateOf<PhysicalMediaFormat?>(null) }
    var downloadedItemIds by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    // Public community shared items from repo
    val communitySharedItems = remember(vaultItems, selectedFormatFilter, searchQuery) {
        vaultItems.filter { item ->
            val isPublic = item.isCommunityShared || !item.isPrivate || item.fingerprint.isDownloadedFromElsewhere
            val matchesFmt = selectedFormatFilter == null || item.format == selectedFormatFilter
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.creatorOrArtist.contains(searchQuery, ignoreCase = true)
            isPublic && matchesFmt && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B111E))
            .padding(14.dp)
            .testTag("community_public_library_section")
    ) {
        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = WarmAmber.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, WarmAmber),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📚", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Community Public Library",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Open Physical Media Exchange & Digital Fingerprinted Downloads",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Browse physical vinyl rips, CD rips, 4K disc archives, and scanned book replicas shared by verified community curators. Downloaded files are saved to your personal vault and opened in Waves/Stream.",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Public Library (Vinyl, CDs, DVDs, Books)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.LightGray)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WarmAmber,
                unfocusedBorderColor = Color(0xFF1E293B)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Format Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedFormatFilter == null,
                    onClick = { selectedFormatFilter = null },
                    label = { Text("All Library (${communitySharedItems.size})") }
                )
            }
            items(PhysicalMediaFormat.entries) { fmt ->
                val count = communitySharedItems.count { it.format == fmt }
                FilterChip(
                    selected = selectedFormatFilter == fmt,
                    onClick = { selectedFormatFilter = fmt },
                    label = { Text("${fmt.iconEmoji} ${fmt.label} ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Library Items List
        if (communitySharedItems.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162032))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📦", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Shared Media Found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Switch items in your Profile Media Vault to 'Community Public' to publish media here!",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                items(communitySharedItems, key = { it.id }) { item ->
                    val isDownloaded = downloadedItemIds.contains(item.id) || item.fingerprint.isDownloadedFromElsewhere

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                        border = BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                        color = Color(0xFF0F172A),
                                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.6f)),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(item.format.iconEmoji, fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "by ${item.creatorOrArtist} • ${item.totalDurationOrPages}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = WarmAmber
                                        )
                                    }
                                }

                                if (item.proofOfPurchase.isVerified) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = NeonCyan.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "🛡️ Verified Owner",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Provenance Fingerprint Indicator
                            ProvenanceFingerprintBanner(
                                item = item,
                                onOpenTransformativeStudio = {}
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Download & Play Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Original Curator: ${item.fingerprint.originalUploaderName}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                Row {
                                    Button(
                                        onClick = {
                                            PhysicalMediaRepository.downloadCommunityMedia(item)
                                            downloadedItemIds = downloadedItemIds + item.id
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDownloaded) Color(0xFF1E293B) else WarmAmber
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                                            contentDescription = null,
                                            tint = if (isDownloaded) NeonCyan else Color.Black,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isDownloaded) "Downloaded" else "Download to Vault",
                                            fontSize = 11.sp,
                                            color = if (isDownloaded) NeonCyan else Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Button(
                                        onClick = { onPlayInPlayer(item) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
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
